package com.bizmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bizmanager.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isArchived = 0 ORDER BY isFavorite DESC, name ASC")
    fun observeActive(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isArchived = 0 AND categoryId = :categoryId ORDER BY name ASC")
    fun observeByCategory(categoryId: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isArchived = 0 AND isFavorite = 1 ORDER BY name ASC")
    fun observeFavorites(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isArchived = 0 AND name LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<ProductEntity>>

    @Insert
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): ProductEntity?

    @Query("UPDATE products SET isArchived = 1 WHERE id = :id")
    suspend fun archive(id: Long)
}
