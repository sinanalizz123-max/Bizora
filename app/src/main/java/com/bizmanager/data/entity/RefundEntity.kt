package com.bizmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "refunds")
data class RefundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val amount: Double = 0.0,
    val reason: String? = null,
    val paymentMethod: String = "Cash",
    val isFullRefund: Boolean = true,
    val refundTransactionNumber: String
)
