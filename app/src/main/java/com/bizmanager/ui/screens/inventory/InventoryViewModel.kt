package com.bizmanager.ui.screens.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizmanager.data.entity.InventoryEntity
import com.bizmanager.data.entity.ProductEntity
import com.bizmanager.data.repository.InventoryRepository
import com.bizmanager.data.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class InventoryViewModel(
    private val inventoryRepository: InventoryRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    val entries: StateFlow<List<Pair<ProductEntity?, InventoryEntity>>> =
        combine(
            inventoryRepository.inventory,
            productRepository.products
        ) { inv, prods ->
            inv.map { entry -> prods.firstOrNull { it.id == entry.productId } to entry }
                .sortedBy { it.first?.name.orEmpty() }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStock: StateFlow<List<InventoryEntity>> = inventoryRepository.lowStock
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun addStock(productId: Long, qty: Double) =
        inventoryRepository.addStock(productId, qty)

    suspend fun removeStock(productId: Long, qty: Double) =
        inventoryRepository.removeStock(productId, qty)
}
