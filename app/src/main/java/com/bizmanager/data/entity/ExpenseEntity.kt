package com.bizmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String = "Other",
    val amount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null
)
