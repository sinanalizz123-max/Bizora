package com.bizmanager.ui.screens.offers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizmanager.data.entity.OfferEntity
import com.bizmanager.data.repository.OfferRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class OffersViewModel(private val repository: OfferRepository) : ViewModel() {

    val offers: StateFlow<List<OfferEntity>> = repository.offers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun addOffer(offer: OfferEntity): Long = repository.addOffer(offer)
    suspend fun toggleActive(offer: OfferEntity) = repository.setActive(offer.id, !offer.isActive)
    suspend fun deleteOffer(offer: OfferEntity) = repository.deleteOffer(offer)
}

object OfferType {
    const val PERCENTAGE = "Percentage"
    const val FLAT = "Flat"
}
