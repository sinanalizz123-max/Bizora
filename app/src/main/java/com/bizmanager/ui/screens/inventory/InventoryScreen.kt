package com.bizmanager.ui.screens.inventory

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.launch

@Composable
fun InventoryScreen(mainViewModel: MainViewModel) {
    val app = mainViewModel.getApplication<BusinessManagerApp>()
    val vm: InventoryViewModel = viewModel(factory = InventoryVMFactory(app))
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val entries by vm.entries.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var targetProductId by remember { mutableStateOf<Long?>(null) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                Text("Inventory", style = MaterialTheme.typography.headlineMedium)
            }
            if (entries.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No stock tracked yet")
                    Text(
                        "Stock entries appear once a product has stock added. Add stock from the Products area.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entries, key = { it.second.id }) { (product, entry) ->
                        Card {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(product?.name ?: "Unknown", fontWeight = FontWeight.Bold)
                                    Text(
                                        "${entry.quantity} ${entry.unit ?: ""}",
                                        fontWeight = FontWeight.Bold,
                                        color = if (entry.quantity <= entry.lowStockThreshold && entry.lowStockThreshold > 0)
                                            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (entry.lowStockThreshold > 0 && entry.quantity <= entry.lowStockThreshold) {
                                    Text("Low stock", color = MaterialTheme.colorScheme.error)
                                }
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = {
                                        targetProductId = product?.id
                                        showAdd = true
                                    }) { Text("Add Stock") }
                                    TextButton(onClick = {
                                        targetProductId = product?.id
                                        showAdd = true
                                    }) { Text("Remove Stock") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAdd && targetProductId != null) {
        StockDialog(
            title = "Update Stock",
            onDismiss = { showAdd = false },
            onConfirm = { qty, add ->
                scope.launch {
                    if (add) vm.addStock(targetProductId!!, qty)
                    else vm.removeStock(targetProductId!!, qty)
                    Toast.makeText(context, "Stock updated", Toast.LENGTH_SHORT).show()
                    showAdd = false
                }
            }
        )
    }
}

@Composable
private fun StockDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (Double, Boolean) -> Unit
) {
    var qty by remember { mutableStateOf("") }
    var add by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Row {
                    TextButton(onClick = { add = true }) { Text("Add") }
                    TextButton(onClick = { add = false }) { Text("Remove") }
                }
                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { qty.toDoubleOrNull()?.let { onConfirm(it, add) } },
                enabled = qty.toDoubleOrNull() != null
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
