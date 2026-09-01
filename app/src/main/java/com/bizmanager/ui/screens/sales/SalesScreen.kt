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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bizmanager.MainViewModel
import com.bizmanager.data.entity.SaleEntity
import com.bizmanager.util.BluetoothPrinter
import com.bizmanager.util.ReceiptData
import com.bizmanager.util.ReceiptFormatter
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
            mainViewModel = mainViewModel,
            onDismiss = { selectedSale = null }
        )
    }
}

@Composable
private fun SaleDetailsDialog(
    sale: SaleEntity,
    mainViewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showRefund by remember { mutableStateOf(false) }
    var showReceipt by remember { mutableStateOf(false) }
    var printing by remember { mutableStateOf(false) }
    var printError by remember { mutableStateOf<String?>(null) }
    var receiptItems by remember { mutableStateOf<List<com.bizmanager.data.entity.SaleItemEntity>>(emptyList()) }
    val business by mainViewModel.businessRepository.business.collectAsState(initial = null)
    LaunchedEffect(showReceipt, sale.id) {
        if (showReceipt) receiptItems = mainViewModel.salesRepository.getSaleItems(sale.id)
    }
    fun printReceipt() {
        if (printing) return
        printing = true
        printError = null
        scope.launch {
            try {
                val items = if (receiptItems.isEmpty()) {
                    mainViewModel.salesRepository.getSaleItems(sale.id).also { receiptItems = it }
                } else receiptItems
                val data = ReceiptData(
                    businessName = business?.name ?: "Business",
                    businessPhone = business?.phone ?: "",
                    businessAddress = business?.address ?: "",
                    sale = sale,
                    items = items,
                    receiptFooter = business?.receiptFooter ?: "Thank you for your visit!"
                )
                val result = BluetoothPrinter(context).print(ReceiptFormatter.toPrinterLines(data))
                result.exceptionOrNull()?.let { printError = it.message ?: "Print failed" }
            } catch (e: Exception) {
                printError = e.message ?: "Print failed"
            } finally {
                printing = false
            }
        }
    }
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
                TextButton(onClick = { showReceipt = !showReceipt }) {
                    Text(if (showReceipt) "Hide Receipt" else "View Receipt")
                }
                TextButton(onClick = ::printReceipt, enabled = !printing) {
                    Text(if (printing) "Printing…" else "Print")
                }
                if (printError != null) {
                    Text(printError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                if (showReceipt) {
                    Spacer(Modifier.height(8.dp))
                    ReceiptContent(
                        sale = sale,
                        items = receiptItems,
                        businessName = business?.name ?: "",
                        businessPhone = business?.phone ?: "",
                        businessAddress = business?.address ?: "",
                        receiptFooter = business?.receiptFooter ?: "Thank you for your visit!"
                    )
                }
                if (showRefund) {
                    Spacer(Modifier.height(8.dp))
                    Text("Refund the full amount of this sale?")
                    TextButton(onClick = {
                        scope.launch {
                            mainViewModel.salesRepository.addRefund(
                                com.bizmanager.data.entity.RefundEntity(
                                    saleId = sale.id,
                                    amount = sale.total,
                                    paymentMethod = sale.paymentMethod,
                                    isFullRefund = true,
                                    refundTransactionNumber = mainViewModel.salesRepository.nextRefundNumber()
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

@Composable
private fun ReceiptContent(
    sale: SaleEntity,
    items: List<com.bizmanager.data.entity.SaleItemEntity>,
    businessName: String,
    businessPhone: String,
    businessAddress: String,
    receiptFooter: String
) {
    Column {
        Text(businessName.ifBlank { "Business" }, fontWeight = FontWeight.Bold)
        if (businessPhone.isNotBlank()) Text(businessPhone, style = MaterialTheme.typography.bodySmall)
        if (businessAddress.isNotBlank()) Text(businessAddress, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        Text("Inv: ${sale.transactionNumber}", fontWeight = FontWeight.Medium)
        Text("Date: ${formatTime(sale.timestamp)}", style = MaterialTheme.typography.bodySmall)
        Text("Payment: ${sale.paymentMethod}", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.productSnapshot, style = MaterialTheme.typography.bodySmall)
                    Text("${qty(item.quantity)} x $%.2f".format(item.unitPrice), style = MaterialTheme.typography.bodySmall)
                }
                Text("$%.2f".format(item.lineTotal), style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Subtotal: $%.2f".format(sale.subtotal))
        if (sale.discount > 0) Text("Discount: -$%.2f".format(sale.discount))
        if (sale.taxTotal > 0) Text("Tax: $%.2f".format(sale.taxTotal))
        Text("Total: $%.2f".format(sale.total), fontWeight = FontWeight.Bold)
        if (sale.amountReceived > 0) {
            Text("Paid: $%.2f".format(sale.amountReceived))
            Text("Change: $%.2f".format(sale.changeDue))
        }
        Spacer(Modifier.height(8.dp))
        Text(receiptFooter, style = MaterialTheme.typography.bodySmall)
    }
}

private fun qty(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else "%.2f".format(value)
