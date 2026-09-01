package com.bizmanager.ui.screens.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bizmanager.MainViewModel
import com.bizmanager.data.entity.SaleEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SalesScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    val sales = mainViewModel.salesRepository.sales
    val salesList by sales.collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }
    var selectedSale by remember { mutableStateOf<SaleEntity?>(null) }

    val filtered = if (query.isBlank()) salesList else salesList.filter {
        it.transactionNumber.contains(query, true)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search by transaction number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                singleLine = true
            )
            if (filtered.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No sales yet")
                    Text("Completed sales will appear here.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { sale ->
                        Card {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(sale.transactionNumber, fontWeight = FontWeight.Bold)
                                    Text(
                                        "$%.2f".format(sale.total),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    formatTime(sale.timestamp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(sale.paymentMethod, style = MaterialTheme.typography.bodySmall)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = { selectedSale = sale }) { Text("Details") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedSale != null) {
        SaleDetailsDialog(
            sale = selectedSale!!,
            salesRepository = mainViewModel.salesRepository,
            onDismiss = { selectedSale = null }
        )
    }
}

@Composable
private fun SaleDetailsDialog(
    sale: SaleEntity,
    salesRepository: com.bizmanager.data.repository.SalesRepository,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var showRefund by remember { mutableStateOf(false) }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(sale.transactionNumber) },
        text = {
            Column {
                Text("Date: ${formatTime(sale.timestamp)}")
                Text("Payment: ${sale.paymentMethod}")
                Text("Subtotal: $%.2f".format(sale.subtotal))
                Text("Discount: $%.2f".format(sale.discount))
                Text("Tax: $%.2f".format(sale.taxTotal))
                Text("Total: $%.2f".format(sale.total), fontWeight = FontWeight.Bold)
                if (showRefund) {
                    Spacer(Modifier.height(8.dp))
                    Text("Refund the full amount of this sale?")
                    TextButton(onClick = {
                        scope.launch {
                            salesRepository.addRefund(
                                com.bizmanager.data.entity.RefundEntity(
                                    saleId = sale.id,
                                    amount = sale.total,
                                    paymentMethod = sale.paymentMethod,
                                    isFullRefund = true,
                                    refundTransactionNumber = salesRepository.nextRefundNumber()
                                )
                            )
                            showRefund = false
                            onDismiss()
                        }
                    }) { Text("Confirm Refund") }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                showRefund = !showRefund
            }) { Text(if (showRefund) "Cancel Refund" else "Refund") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

private fun formatTime(ts: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(ts))
