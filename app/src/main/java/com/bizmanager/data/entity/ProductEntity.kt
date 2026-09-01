package com.bizmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryId: Long? = null,
    val imageUri: String? = null,
    val price: Double = 0.0,
    val costPrice: Double? = null,
    val sku: String? = null,
    val barcode: String? = null,
    val unit: String? = null,
    val taxRate: Double = 0.0,
    val isWeightBased: Boolean = false,
    val isFavorite: Boolean = false,
    val notes: String? = null,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
