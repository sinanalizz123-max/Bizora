package com.bizmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business")
data class BusinessEntity(
    @PrimaryKey val id: Long = 1,
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val email: String = "",
    val logoUri: String? = null,
    val currency: String = "₹",
    val taxPreference: String = "inclusive",
    val businessType: String = "general",
    val businessTypeLabel: String = "General / Other",
    val receiptFooter: String = "Thank you for your visit!"
)
