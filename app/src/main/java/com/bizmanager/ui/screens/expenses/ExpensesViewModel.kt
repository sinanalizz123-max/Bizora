package com.bizmanager.ui.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizmanager.data.entity.ExpenseEntity
import com.bizmanager.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ExpensesViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val _query = MutableStateFlow("")

    val expenses: StateFlow<List<ExpenseEntity>> =
        combine(repository.expenses, _query) { list, q ->
            if (q.isBlank()) list
            else list.filter {
                it.category.contains(q, true) || it.notes?.contains(q, true) == true
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(q: String) { _query.value = q }

    suspend fun addExpense(expense: ExpenseEntity): Long = repository.addExpense(expense)
}
