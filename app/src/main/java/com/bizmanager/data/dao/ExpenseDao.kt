package com.bizmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bizmanager.data.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: ExpenseEntity): Long

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE timestamp >= :start AND timestamp < :end ORDER BY timestamp DESC")
    fun observeBetween(start: Long, end: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE category LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<ExpenseEntity>>
}
