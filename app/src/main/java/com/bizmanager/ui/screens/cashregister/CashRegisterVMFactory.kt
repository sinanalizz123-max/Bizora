package com.bizmanager.ui.screens.cashregister

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bizmanager.BusinessManagerApp

class CashRegisterVMFactory(private val app: BusinessManagerApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CashRegisterViewModel(
            registerRepository = app.container.cashRegisterRepository,
            salesRepository = app.container.salesRepository
        ) as T
    }
}
