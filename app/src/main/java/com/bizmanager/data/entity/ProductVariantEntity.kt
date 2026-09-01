package com.bizmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_variants")
data class ProductVariantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val name: String,
    val price: Double,
    val taxRate: Double = 0.0,
    val barcode: String? = null,
    val sku: String? = null,
    val isFavorite: Boolean = false,
    val sortOrder: Int = 0
)
