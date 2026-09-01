package com.bizmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bizmanager.data.entity.OfferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfferDao {
    @Query("SELECT * FROM offers ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<OfferEntity>>

    @Insert
    suspend fun insert(offer: OfferEntity): Long

    @Update
    suspend fun update(offer: OfferEntity)

    @Delete
    suspend fun delete(offer: OfferEntity)

    @Query("UPDATE offers SET isActive = :active WHERE id = :id")
    suspend fun setActive(id: Long, active: Boolean)
}
