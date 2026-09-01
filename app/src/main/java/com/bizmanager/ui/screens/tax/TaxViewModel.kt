package com.bizmanager.ui.screens.tax

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizmanager.data.entity.TaxEntity
import com.bizmanager.data.repository.TaxRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TaxViewModel(private val repository: TaxRepository) : ViewModel() {

    val taxes: StateFlow<List<TaxEntity>> = repository.taxes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun addTax(tax: TaxEntity): Long = repository.addTax(tax)
    suspend fun resetPresets() {
        repository.deleteAll()
        listOf(
            TaxEntity(name = "No Tax", rate = 0.0, isPreset = true),
            TaxEntity(name = "Standard", rate = getDefaultRate(), isPreset = true)
        ).forEach { repository.addTax(it) }
    }

    private fun getDefaultRate(): Double = 10.0
}
