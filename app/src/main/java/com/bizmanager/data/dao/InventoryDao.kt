package com.bizmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bizmanager.data.entity.InventoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory")
    fun observeAll(): Flow<List<InventoryEntity>>

    @Query("SELECT * FROM inventory WHERE productId = :productId")
    suspend fun getForProduct(productId: Long): InventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(inventory: InventoryEntity)

    @Query("SELECT * FROM inventory WHERE quantity <= lowStockThreshold")
    fun observeLowStock(): Flow<List<InventoryEntity>>
}
