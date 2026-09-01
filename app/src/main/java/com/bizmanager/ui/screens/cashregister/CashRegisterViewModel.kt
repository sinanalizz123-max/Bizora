package com.bizmanager.ui.screens.cashregister

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizmanager.data.entity.CashRegisterEntity
import com.bizmanager.data.entity.RefundEntity
import com.bizmanager.data.entity.SaleEntity
import com.bizmanager.data.repository.CashRegisterRepository
import com.bizmanager.data.repository.SalesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CashRegisterViewModel(
    private val registerRepository: CashRegisterRepository,
    private val salesRepository: SalesRepository
) : ViewModel() {

    val entries: StateFlow<List<CashRegisterEntity>> = registerRepository.entries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cashSalesTotal: StateFlow<Double> = combine(
        salesRepository.sales,
        entries
    ) { sales, _ ->
        sales.filter { it.paymentMethod.equals("Cash", true) }.sumOf { it.total }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val refundsTotal: StateFlow<Double> = salesRepository.refunds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        .let { refundsFlow ->
            combine(refundsFlow, entries) { refunds, _ ->
                refunds.sumOf { it.amount }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
        }

    val registerBalance: StateFlow<Double> = combine(
        registerRepository.entries,
        cashSalesTotal,
        refundsTotal
    ) { entries, sales, refunds ->
        val opening = entries.filter { it.type == RegisterKind.OPENING }.sumOf { it.amount }
        val cashIn = entries.filter { it.type == RegisterKind.CASH_IN }.sumOf { it.amount }
        val cashOut = entries.filter { it.type == RegisterKind.CASH_OUT }.sumOf { it.amount }
        opening + sales - refunds + cashIn - cashOut
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    suspend fun setOpening(amount: Double): Long =
        registerRepository.addEntry(CashRegisterEntity(type = RegisterKind.OPENING, amount = amount))

    suspend fun adjust(type: String, amount: Double, note: String?): Long =
        registerRepository.addEntry(CashRegisterEntity(type = type, amount = amount, note = note))
}

object RegisterKind {
    const val OPENING = "Opening"
    const val CASH_IN = "Cash In"
    const val CASH_OUT = "Cash Out"
}
