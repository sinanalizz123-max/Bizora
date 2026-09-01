package com.bizmanager.data.repository

import com.bizmanager.data.dao.BusinessDao
import com.bizmanager.data.entity.BusinessEntity
import kotlinx.coroutines.flow.Flow

class BusinessRepository(private val dao: BusinessDao) {

    val business: Flow<BusinessEntity?> = dao.getBusiness()

    suspend fun getBusinessOnce(): BusinessEntity? = dao.getBusinessOnce()

    suspend fun save(business: BusinessEntity) = dao.upsert(business)

    suspend fun ensureSeeded() {
        if (dao.getBusinessOnce() == null) {
            dao.upsert(BusinessEntity())
        }
    }
}
