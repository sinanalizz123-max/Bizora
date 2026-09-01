package com.bizmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bizmanager.data.entity.CashRegisterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegisterDao {
    @Insert
    suspend fun insert(entry: CashRegisterEntity): Long

    @Query("SELECT * FROM register_entries ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<CashRegisterEntity>>

    @Query("SELECT * FROM register_entries WHERE type = :type ORDER BY timestamp DESC")
    fun observeByType(type: String): Flow<List<CashRegisterEntity>>

    @Query("SELECT * FROM register_entries WHERE type = :type ORDER BY timestamp ASC LIMIT 1")
    suspend fun latestOfType(type: String): CashRegisterEntity?
}
