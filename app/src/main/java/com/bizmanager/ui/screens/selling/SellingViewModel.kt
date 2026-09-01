package com.bizmanager.ui.screens.selling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizmanager.data.entity.CategoryEntity
import com.bizmanager.data.entity.CustomerEntity
import com.bizmanager.data.entity.ProductEntity
import com.bizmanager.data.entity.ProductVariantEntity
import com.bizmanager.data.repository.ProductRepository
import com.bizmanager.data.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SellingViewModel(
    private val productRepository: ProductRepository,
    private val salesRepository: SalesRepository
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = productRepository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<ProductEntity>> = productRepository.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<CustomerEntity>> = salesRepository.customers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart

    private val _selectedCategory = MutableStateFlow<Long?>(null)
    val selectedCategory: StateFlow<Long?> = _selectedCategory

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val displayedProducts: StateFlow<List<Pair<ProductEntity, List<ProductVariantEntity>>>> =
        combine(products, _selectedCategory, _searchQuery) { prods, cat, query ->
            val filtered = if (cat == null) prods else prods.filter { it.categoryId == cat }
            val q = query.trim()
        val byName = if (q.isBlank()) filtered else filtered.filter { it.name.contains(q, true) }
        byName.sortedBy { !it.isFavorite }.map { it to emptyList<ProductVariantEntity>() }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCustomer = MutableStateFlow<CustomerEntity?>(null)
    val selectedCustomer: StateFlow<CustomerEntity?> = _selectedCustomer

    private val _cartDiscount = MutableStateFlow(0.0)
    val cartDiscount: StateFlow<Double> = _cartDiscount

    val cartSummary = combine(cart, _cartDiscount) { items, discount ->
        val subtotal = items.sumOf { it.lineSubtotal }
        val discount = discount.coerceAtMost(subtotal)
        var tax = 0.0
        var taxableSubtotal = subtotal - discount
        if (taxableSubtotal > 0) {
            val discountedPerItem = items.map {
                val proportion = if (subtotal > 0) it.lineSubtotal / subtotal else 0.0
                val itemDisc = discount * proportion
                it.copy(unitPrice = it.unitPrice, quantity = it.quantity)
            }
            tax = discountedPerItem.sumOf {
                val discountedLine = it.unitPrice * it.quantity - discount * (if (subtotal > 0) (it.lineSubtotal / subtotal) else 0.0)
                discountedLine * it.taxRate / 100.0
            }
        }
        CartSummary(
            subtotal = subtotal,
            discount = discount,
            tax = tax,
            total = subtotal - discount + tax
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartSummary(0.0, 0.0, 0.0, 0.0))

    fun selectCategory(id: Long?) { _selectedCategory.value = id }
    fun setSearch(q: String) { _searchQuery.value = q }

    fun addToCart(product: ProductEntity) {
        _cart.value = _cart.value.toMutableList().also {
            val existing = it.indexOfFirst { c -> c.productId == product.id && c.variantId == null }
            if (existing >= 0) {
                val item = it[existing]
                it[existing] = item.copy(quantity = item.quantity + 1)
            } else {
                it.add(
                    CartItem(
                        productId = product.id,
                        productName = product.name,
                        unitPrice = product.price,
                        taxRate = product.taxRate,
                        isWeightBased = product.isWeightBased
                    )
                )
            }
        }
    }

    fun addVariantToCart(product: ProductEntity, variant: ProductVariantEntity) {
        _cart.value = _cart.value.toMutableList().also {
            val existing = it.indexOfFirst { c -> c.variantId == variant.id }
            if (existing >= 0) {
                val item = it[existing]
                it[existing] = item.copy(quantity = item.quantity + 1)
            } else {
                it.add(
                    CartItem(
                        productId = product.id,
                        productName = product.name,
                        variantId = variant.id,
                        variantName = variant.name,
                        unitPrice = variant.price,
                        taxRate = variant.taxRate,
                        isWeightBased = false
                    )
                )
            }
        }
    }

    fun setCartQuantity(index: Int, qty: Double) {
        _cart.value = _cart.value.toMutableList().also {
            it[index] = it[index].copy(quantity = qty.coerceAtLeast(0.0))
            if (qty <= 0) it.removeAt(index)
        }
    }

    fun removeFromCart(index: Int) {
        _cart.value = _cart.value.toMutableList().also { it.removeAt(index) }
    }

    fun setCartDiscount(d: Double) { _cartDiscount.value = d.coerceAtLeast(0.0) }
    fun selectCustomer(c: CustomerEntity?) { _selectedCustomer.value = c }
    fun clearCart() { _cart.value = emptyList(); _cartDiscount.value = 0.0 }
}

data class CartSummary(
    val subtotal: Double,
    val discount: Double,
    val tax: Double,
    val total: Double
)
