package com.bizmanager.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizmanager.data.entity.ExpenseEntity
import com.bizmanager.data.entity.RefundEntity
import com.bizmanager.data.entity.SaleEntity
import com.bizmanager.data.entity.SaleItemEntity
import com.bizmanager.data.repository.ExpenseRepository
import com.bizmanager.data.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

enum class ReportPeriod(val label: String) {
    TODAY("Today"),
    WEEK("7 days"),
    MONTH("30 days"),
    ALL("All time")
}

data class ReportKpis(
    val orderCount: Int = 0,
    val revenue: Double = 0.0,
    val expenses: Double = 0.0,
    val refunds: Double = 0.0,
    val profit: Double = 0.0
)

data class ProductSummary(
    val name: String,
    val quantity: Double,
    val revenue: Double
)

data class ReportsUiState(
    val period: ReportPeriod,
    val kpis: ReportKpis,
    val topProducts: List<ProductSummary>,
    val paymentMethods: List<Pair<String, Double>>
)

class ReportsViewModel(
    private val salesRepository: SalesRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val period = MutableStateFlow(ReportPeriod.TODAY)

    private val sales: StateFlow<List<SaleEntity>> = salesRepository.sales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val refunds: StateFlow<List<RefundEntity>> = salesRepository.refunds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val expenses: StateFlow<List<ExpenseEntity>> = expenseRepository.expenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val saleItems: StateFlow<List<SaleItemEntity>> = salesRepository.allSaleItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<ReportsUiState> = combine(
        period, sales, refunds, expenses, saleItems
    ) { period, sales, refunds, expenses, items ->
        val (start, end) = rangeFor(period)
        val periodSales = if (period == ReportPeriod.ALL) sales else sales.filter { it.timestamp in start until end }
        val periodRefunds = if (period == ReportPeriod.ALL) refunds else refunds.filter { it.timestamp in start until end }
        val periodExpenses = if (period == ReportPeriod.ALL) expenses else expenses.filter { it.timestamp in start until end }

        val saleIds = periodSales.map { it.id }.toSet()
        val periodItems = if (period == ReportPeriod.ALL) items else items.filter { it.saleId in saleIds }

        val revenue = periodSales.sumOf { it.total }
        val expenseTotal = periodExpenses.sumOf { it.amount }
        val refundTotal = periodRefunds.sumOf { it.amount }

        val kpis = ReportKpis(
            orderCount = periodSales.size,
            revenue = revenue,
            expenses = expenseTotal,
            refunds = refundTotal,
            profit = revenue - refundTotal - expenseTotal
        )

        val topProducts = periodItems
            .groupBy { it.productSnapshot }
            .map { (name, group) ->
                ProductSummary(
                    name = name,
                    quantity = group.sumOf { it.quantity },
                    revenue = group.sumOf { it.lineTotal - it.taxAmount }
                )
            }
            .sortedWith(compareByDescending<ProductSummary> { it.quantity }.thenByDescending { it.revenue })
            .take(10)

        val paymentMethods = periodSales
            .groupBy { it.paymentMethod }
            .map { (method, group) -> method to group.sumOf { it.total } }
            .sortedByDescending { it.second }

        ReportsUiState(
            period = period,
            kpis = kpis,
            topProducts = topProducts,
            paymentMethods = paymentMethods
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportsUiState(ReportPeriod.TODAY, ReportKpis(), emptyList(), emptyList()))

    fun setPeriod(value: ReportPeriod) {
        period.value = value
    }

    private fun rangeFor(period: ReportPeriod): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        return when (period) {
            ReportPeriod.TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis
                cal.add(Calendar.DAY_OF_YEAR, 1)
                start to cal.timeInMillis
            }
            ReportPeriod.WEEK -> {
                cal.add(Calendar.DAY_OF_YEAR, -7)
                cal.timeInMillis to now
            }
            ReportPeriod.MONTH -> {
                cal.add(Calendar.DAY_OF_YEAR, -30)
                cal.timeInMillis to now
            }
            ReportPeriod.ALL -> 0L to Long.MAX_VALUE
        }
    }
}
