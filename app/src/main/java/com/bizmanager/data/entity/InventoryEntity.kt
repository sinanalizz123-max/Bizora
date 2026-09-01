package com.bizmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val variantId: Long? = null,
    val quantity: Double = 0.0,
    val unit: String? = null,
    val lowStockThreshold: Double = 0.0
)
