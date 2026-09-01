package com.bizmanager.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizmanager.data.entity.CategoryEntity
import com.bizmanager.data.entity.ProductEntity
import com.bizmanager.data.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProductsViewModel(private val repository: ProductRepository) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<ProductEntity>> = repository.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun addProduct(product: ProductEntity): Long = repository.addProduct(product)
    suspend fun updateProduct(product: ProductEntity) = repository.updateProduct(product)
    suspend fun addCategory(name: String): Long = repository.addCategory(name)
    suspend fun archiveProduct(id: Long) = repository.archiveProduct(id)
    suspend fun getVariants(productId: Long) = repository.getVariants(productId)
    suspend fun addVariant(variant: com.bizmanager.data.entity.ProductVariantEntity): Long =
        repository.addVariant(variant)
    suspend fun deleteVariants(productId: Long) = repository.deleteVariantsForProduct(productId)
}
