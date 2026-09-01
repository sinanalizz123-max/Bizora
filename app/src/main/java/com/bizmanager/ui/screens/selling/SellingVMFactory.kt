package com.bizmanager.ui.screens.selling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bizmanager.BusinessManagerApp

class SellingVMFactory(private val app: BusinessManagerApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SellingViewModel(
            productRepository = app.container.productRepository,
            salesRepository = app.container.salesRepository
        ) as T
    }
}
