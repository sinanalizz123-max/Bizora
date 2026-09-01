package com.bizmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val customerId: Long? = null,
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val taxTotal: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String = "Cash",
    val amountReceived: Double = 0.0,
    val changeDue: Double = 0.0,
    val notes: String? = null
)
