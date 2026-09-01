package com.bizmanager.ui.screens.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bizmanager.BusinessManagerApp

class OffersVMFactory(private val app: BusinessManagerApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OffersViewModel(app.container.offerRepository) as T
    }
}
