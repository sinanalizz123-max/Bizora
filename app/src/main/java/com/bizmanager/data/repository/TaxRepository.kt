package com.bizmanager.data.repository

import com.bizmanager.data.dao.TaxDao
import com.bizmanager.data.entity.TaxEntity
import kotlinx.coroutines.flow.Flow

class TaxRepository(private val taxDao: TaxDao) {
    val taxes: Flow<List<TaxEntity>> = taxDao.observeAll()

    suspend fun addTax(tax: TaxEntity): Long = taxDao.insert(tax)
    suspend fun deleteAll() = taxDao.deleteAll()
}
