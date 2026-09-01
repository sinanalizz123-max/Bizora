package com.bizmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bizmanager.data.repository.BusinessRepository
import com.bizmanager.data.repository.ExpenseRepository
import com.bizmanager.data.repository.ProductRepository
import com.bizmanager.data.repository.SalesRepository
import com.bizmanager.data.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val container = (app as BusinessManagerApp).container

    val settings: SettingsManager = container.settings
    val businessRepository: BusinessRepository = container.businessRepository
    val productRepository: ProductRepository = container.productRepository
    val salesRepository: SalesRepository = container.salesRepository
    val expenseRepository: ExpenseRepository = container.expenseRepository

    val onboardingDone: StateFlow<Boolean> = settings.onboardingDone
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val settingsChanged: StateFlow<Int> = settings.settingsChanged
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val sellingEnabled: StateFlow<Boolean> = settings.sellingEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val inventoryEnabled: StateFlow<Boolean> = settings.inventoryEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val customersEnabled: StateFlow<Boolean> = settings.customersEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val expensesEnabled: StateFlow<Boolean> = settings.expensesEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val reportsEnabled: StateFlow<Boolean> = settings.reportsEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    suspend fun ensureSeed() {
        businessRepository.ensureSeeded()
        if (salesRepository.getWalkInCustomer() == null) {
            salesRepository.addCustomer(
                com.bizmanager.data.entity.CustomerEntity(
                    name = "Walk-in Customer",
                    isWalkIn = true
                )
            )
        }
    }

    suspend fun recordSale(
        items: List<com.bizmanager.ui.screens.selling.CartItem>,
        subtotal: Double,
        discount: Double,
        tax: Double,
        total: Double,
        amountReceived: Double,
        customerId: Long?
    ): Long {
        val walkIn = salesRepository.getWalkInCustomer()
        val effectiveCustomer = customerId ?: walkIn?.id
        val sale = com.bizmanager.data.entity.SaleEntity(
            transactionNumber = salesRepository.nextTransactionNumber(),
            customerId = effectiveCustomer,
            subtotal = subtotal,
            discount = discount,
            taxTotal = tax,
            total = total,
            paymentMethod = "Cash",
            amountReceived = amountReceived,
            changeDue = (amountReceived - total).coerceAtLeast(0.0)
        )
        val saleItems = items.map {
            val discountedLine = it.unitPrice * it.quantity -
                discount * (if (subtotal > 0) (it.lineSubtotal / subtotal) else 0.0)
            val taxAmount = discountedLine * it.taxRate / 100.0
            com.bizmanager.data.entity.SaleItemEntity(
                saleId = 0,
                productId = it.productId,
                variantId = it.variantId,
                productSnapshot = if (it.variantName != null) "${it.productName} (${it.variantName})" else it.productName,
                quantity = it.quantity,
                unitPrice = it.unitPrice,
                taxRate = it.taxRate,
                taxAmount = taxAmount,
                discount = discount * (if (subtotal > 0) (it.lineSubtotal / subtotal) else 0.0),
                lineTotal = it.unitPrice * it.quantity - discount * (if (subtotal > 0) (it.lineSubtotal / subtotal) else 0.0) + taxAmount
            )
        }
        return salesRepository.recordSale(sale, saleItems)
    }
}
