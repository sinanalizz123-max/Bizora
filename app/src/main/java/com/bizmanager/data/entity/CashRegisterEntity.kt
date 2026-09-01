package com.bizmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "register_entries")
data class CashRegisterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val amount: Double = 0.0,
    val note: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
