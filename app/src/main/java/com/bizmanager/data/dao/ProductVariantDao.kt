package com.bizmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bizmanager.data.entity.ProductVariantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductVariantDao {
    @Query("SELECT * FROM product_variants WHERE productId = :productId ORDER BY sortOrder ASC")
    fun observeForProduct(productId: Long): Flow<List<ProductVariantEntity>>

    @Query("SELECT * FROM product_variants WHERE productId = :productId ORDER BY sortOrder ASC")
    suspend fun getForProduct(productId: Long): List<ProductVariantEntity>

    @Query("SELECT * FROM product_variants WHERE id = :id")
    suspend fun getById(id: Long): ProductVariantEntity?

    @Insert
    suspend fun insert(variant: ProductVariantEntity): Long

    @Update
    suspend fun update(variant: ProductVariantEntity)

    @Query("DELETE FROM product_variants WHERE productId = :productId")
    suspend fun deleteForProduct(productId: Long)
}
