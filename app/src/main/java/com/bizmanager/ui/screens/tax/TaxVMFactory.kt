package com.bizmanager.ui.screens.tax

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bizmanager.BusinessManagerApp

class TaxVMFactory(private val app: BusinessManagerApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaxViewModel(app.container.taxRepository) as T
    }
}
