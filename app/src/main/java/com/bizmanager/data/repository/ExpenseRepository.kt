package com.bizmanager.data.repository

import com.bizmanager.data.dao.ExpenseDao
import com.bizmanager.data.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {
    val expenses: Flow<List<ExpenseEntity>> = dao.observeAll()

    fun expensesBetween(start: Long, end: Long): Flow<List<ExpenseEntity>> =
        dao.observeBetween(start, end)

    suspend fun addExpense(expense: ExpenseEntity): Long = dao.insert(expense)
}
