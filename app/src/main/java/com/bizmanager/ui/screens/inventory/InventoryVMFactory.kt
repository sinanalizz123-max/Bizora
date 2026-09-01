package com.bizmanager.ui.screens.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bizmanager.BusinessManagerApp

class InventoryVMFactory(private val app: BusinessManagerApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InventoryViewModel(
            inventoryRepository = app.container.inventoryRepository,
            productRepository = app.container.productRepository
        ) as T
    }
}
