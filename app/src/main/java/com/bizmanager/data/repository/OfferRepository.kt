package com.bizmanager.data.repository

import com.bizmanager.data.dao.OfferDao
import com.bizmanager.data.entity.OfferEntity
import kotlinx.coroutines.flow.Flow

class OfferRepository(private val offerDao: OfferDao) {
    val offers: Flow<List<OfferEntity>> = offerDao.observeAll()

    suspend fun addOffer(offer: OfferEntity): Long = offerDao.insert(offer)
    suspend fun updateOffer(offer: OfferEntity) = offerDao.update(offer)
    suspend fun deleteOffer(offer: OfferEntity) = offerDao.delete(offer)
    suspend fun setActive(id: Long, active: Boolean) = offerDao.setActive(id, active)
}
