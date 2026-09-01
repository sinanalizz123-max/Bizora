package com.bizmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bizmanager.data.entity.SaleEntity
import com.bizmanager.data.entity.SaleItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Insert
    suspend fun insertSale(sale: SaleEntity): Long

    @Insert
    suspend fun insertItems(items: List<SaleItemEntity>)

    @Query("SELECT * FROM sales ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE timestamp >= :start AND timestamp < :end ORDER BY timestamp DESC")
    fun observeBetween(start: Long, end: Long): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getById(id: Long): SaleEntity?

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    suspend fun getItems(saleId: Long): List<SaleItemEntity>

    @Query("SELECT * FROM sale_items")
    fun observeAllItems(): Flow<List<SaleItemEntity>>

    @Query("SELECT COUNT(*) FROM sales WHERE id = :id")
    suspend fun countById(id: Long): Int

    @Query("SELECT * FROM sales WHERE transactionNumber LIKE '%' || :query || '%'")
    fun searchByNumber(query: String): Flow<List<SaleEntity>>

    @Query("SELECT MAX(id) FROM sales")
    suspend fun getMaxId(): Long?
}
