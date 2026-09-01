package com.bizmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_movements")
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val variantId: Long? = null,
    val type: String,
    val quantity: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null
)
