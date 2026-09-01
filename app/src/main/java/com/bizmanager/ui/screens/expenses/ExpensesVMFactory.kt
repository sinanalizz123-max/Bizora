package com.bizmanager.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bizmanager.BusinessManagerApp

class ExpensesVMFactory(private val app: BusinessManagerApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExpensesViewModel(app.container.expenseRepository) as T
    }
}
