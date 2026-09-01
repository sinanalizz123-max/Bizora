package com.bizmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bizmanager.data.entity.RefundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RefundDao {
    @Insert
    suspend fun insert(refund: RefundEntity): Long

    @Query("SELECT * FROM refunds WHERE saleId = :saleId ORDER BY timestamp DESC")
    fun observeForSale(saleId: Long): Flow<List<RefundEntity>>

    @Query("SELECT * FROM refunds ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<RefundEntity>>
}
