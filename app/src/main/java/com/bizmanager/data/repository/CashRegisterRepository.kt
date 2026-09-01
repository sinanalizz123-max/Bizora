package com.bizmanager.data.repository

import com.bizmanager.data.dao.RegisterDao
import com.bizmanager.data.entity.CashRegisterEntity
import kotlinx.coroutines.flow.Flow

class CashRegisterRepository(private val registerDao: RegisterDao) {

    val entries: Flow<List<CashRegisterEntity>> = registerDao.observeAll()
    val openings: Flow<List<CashRegisterEntity>> = registerDao.observeByType(CASH_OPENING)
    val adjustments: Flow<List<CashRegisterEntity>> = registerDao.observeByType(CASH_IN)

    suspend fun addEntry(entry: CashRegisterEntity): Long = registerDao.insert(entry)
    suspend fun latestOpening(): CashRegisterEntity? = registerDao.latestOfType(CASH_OPENING)

    companion object {
        const val CASH_OPENING = "Opening"
        const val CASH_IN = "Cash In"
        const val CASH_OUT = "Cash Out"
    }
}
