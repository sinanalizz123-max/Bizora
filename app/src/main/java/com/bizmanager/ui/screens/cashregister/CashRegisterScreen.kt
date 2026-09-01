package com.bizmanager.ui.screens.cashregister

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bizmanager.BusinessManagerApp
import com.bizmanager.MainViewModel
import com.bizmanager.data.entity.CashRegisterEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CashRegisterScreen(mainViewModel: MainViewModel) {
    val app = mainViewModel.getApplication<BusinessManagerApp>()
    val vm: CashRegisterViewModel = viewModel(factory = CashRegisterVMFactory(app))
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val entries by vm.entries.collectAsState()
    val cashSales by vm.cashSalesTotal.collectAsState()
    val refunds by vm.refundsTotal.collectAsState()
    val balance by vm.registerBalance.collectAsState()

    var showOpening by remember { mutableStateOf(false) }
    var showCashIn by remember { mutableStateOf(false) }
    var showCashOut by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCashIn = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add cash in")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Cash Register", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Expected drawer", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "$%.2f".format(balance),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Cash sales", style = MaterialTheme.typography.bodySmall)
                            Text("$%.2f".format(cashSales), fontWeight = FontWeight.Medium)
                            Text("Refunds", style = MaterialTheme.typography.bodySmall)
                            Text("$%.2f".format(refunds), fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showOpening = true }) { Text("Set opening") }
                        OutlinedButton(onClick = { showCashOut = true }) { Text("Cash out") }
                    }
                }
            }

            if (entries.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No register activity yet")
                    Text(
                        "Set your opening balance to start tracking the drawer.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entries, key = { it.id }) { entry ->
                        RegisterEntryRow(entry, dateFormat)
                    }
                }
            }
        }
    }

    if (showOpening) {
        RegisterAmountDialog(
            title = "Set opening balance",
            onDismiss = { showOpening = false },
            onSave = { amount ->
                scope.launch {
                    vm.setOpening(amount)
                    Toast.makeText(context, "Opening balance set", Toast.LENGTH_SHORT).show()
                    showOpening = false
                }
            }
        )
    }
    if (showCashIn) {
        RegisterAmountDialog(
            title = "Cash in",
            onDismiss = { showCashIn = false },
            onSave = { amount ->
                scope.launch {
                    vm.adjust(RegisterKind.CASH_IN, amount, "Cash in adjustment")
                    Toast.makeText(context, "Cash in recorded", Toast.LENGTH_SHORT).show()
                    showCashIn = false
                }
            }
        )
    }
    if (showCashOut) {
        RegisterAmountDialog(
            title = "Cash out",
            onDismiss = { showCashOut = false },
            onSave = { amount ->
                scope.launch {
                    vm.adjust(RegisterKind.CASH_OUT, amount, "Cash out adjustment")
                    Toast.makeText(context, "Cash out recorded", Toast.LENGTH_SHORT).show()
                    showCashOut = false
                }
            }
        )
    }
}

@Composable
private fun RegisterEntryRow(entry: CashRegisterEntity, dateFormat: SimpleDateFormat) {
    val isOut = entry.type == RegisterKind.CASH_OUT
    val color = when {
        isOut -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.type, fontWeight = FontWeight.Bold)
                Text(
                    dateFormat.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.bodySmall
                )
                if (entry.note != null) Text(entry.note, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                "$%.2f".format(entry.amount),
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun RegisterAmountDialog(
    title: String,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { amount.toDoubleOrNull()?.let(onSave) },
                enabled = amount.toDoubleOrNull() != null
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
