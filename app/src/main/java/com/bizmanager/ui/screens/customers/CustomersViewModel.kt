package com.bizmanager.ui.screens.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizmanager.data.entity.CustomerEntity
import com.bizmanager.data.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CustomersViewModel(private val salesRepository: SalesRepository) : ViewModel() {

    private val _query = MutableStateFlow("")

    val customers: StateFlow<List<CustomerEntity>> =
        combine(salesRepository.customers, _query) { list, q ->
            if (q.isBlank()) list
            else list.filter {
                it.name.contains(q, true) || it.phone?.contains(q, true) == true
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(q: String) { _query.value = q }

    suspend fun addCustomer(customer: CustomerEntity): Long =
        salesRepository.addCustomer(customer)
}
