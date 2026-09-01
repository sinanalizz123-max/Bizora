package com.bizmanager.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bizmanager.BusinessManagerApp

class ProductsVMFactory(private val app: BusinessManagerApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductsViewModel(app.container.productRepository) as T
    }
}
