package com.bizmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bizmanager.data.entity.BusinessEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {
    @Query("SELECT * FROM business WHERE id = 1")
    fun getBusiness(): Flow<BusinessEntity?>

    @Query("SELECT * FROM business WHERE id = 1")
    suspend fun getBusinessOnce(): BusinessEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(business: BusinessEntity)

    @Query("UPDATE business SET name = :name, phone = :phone, address = :address, email = :email, logoUri = :logoUri, currency = :currency, taxPreference = :taxPreference, receiptFooter = :receiptFooter WHERE id = 1")
    suspend fun updateDetails(
        name: String,
        phone: String,
        address: String,
        email: String,
        logoUri: String?,
        currency: String,
        taxPreference: String,
        receiptFooter: String
    )
}
