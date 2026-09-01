package com.bizmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bizmanager.data.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockMovementDao {
    @Insert
    suspend fun insert(movement: StockMovementEntity): Long

    @Query("SELECT * FROM stock_movements WHERE productId = :productId ORDER BY timestamp DESC")
    fun observeForProduct(productId: Long): Flow<List<StockMovementEntity>>

    @Query("SELECT * FROM stock_movements ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<StockMovementEntity>>
}
