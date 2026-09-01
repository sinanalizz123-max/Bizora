package com.bizmanager.ui.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bizmanager.BusinessManagerApp
import com.bizmanager.MainViewModel

@Composable
fun ReportsScreen(mainViewModel: MainViewModel) {
    val app = mainViewModel.getApplication<BusinessManagerApp>()
    val vm: ReportsViewModel = viewModel(factory = ReportsVMFactory(app))
    val state by vm.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Reports", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ReportPeriod.entries.forEach { p ->
                    TextButton(
                        onClick = { vm.setPeriod(p) },
                        enabled = state.period != p
                    ) {
                        Text(p.label)
                    }
                }
            }
        }
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Overview (${state.period.label})", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    MetricRow("Orders", state.kpis.orderCount.toString())
                    MetricRow("Revenue", money(state.kpis.revenue))
                    MetricRow("Expenses", money(state.kpis.expenses))
                    MetricRow("Refunds", money(state.kpis.refunds))
                    MetricRow("Profit", money(state.kpis.profit))
                }
            }
        }
        if (state.topProducts.isNotEmpty()) {
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Top products", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(4.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        state.topProducts.forEach { p ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(p.name, modifier = Modifier.weight(1f))
                                Text("${trimQty(p.quantity)}", fontWeight = FontWeight.Medium)
                                Text(money(p.revenue))
                            }
                        }
                    }
                }
            }
        }
        if (state.paymentMethods.isNotEmpty()) {
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("By payment method", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        state.paymentMethods.forEach { (method, amount) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(method, modifier = Modifier.weight(1f))
                                Text(money(amount))
                            }
                        }
                    }
                }
            }
        }
        if (state.kpis.orderCount == 0 && state.topProducts.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No activity for this period")
                    Text("Record sales to see insights here.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

private fun money(value: Double): String = "$%.2f".format(value)

private fun trimQty(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else "%.2f".format(value)
