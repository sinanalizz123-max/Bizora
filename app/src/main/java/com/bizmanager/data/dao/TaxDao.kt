package com.bizmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bizmanager.data.entity.TaxEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaxDao {
    @Query("SELECT * FROM taxes ORDER BY rate ASC")
    fun observeAll(): Flow<List<TaxEntity>>

    @Insert
    suspend fun insert(tax: TaxEntity): Long

    @Query("DELETE FROM taxes")
    suspend fun deleteAll()
}
