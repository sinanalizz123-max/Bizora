package com.bizmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "taxes")
data class TaxEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val rate: Double,
    val isPreset: Boolean = false
)
