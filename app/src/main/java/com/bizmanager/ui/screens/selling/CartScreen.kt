package com.bizmanager.ui.screens.selling

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bizmanager.data.entity.CustomerEntity

@Composable
fun CartScreen(
    cart: List<CartItem>,
    summary: CartSummary,
    customers: List<CustomerEntity>,
    selectedCustomer: CustomerEntity?,
    onSelectCustomer: (CustomerEntity?) -> Unit,
    onBack: () -> Unit,
    onQuantityChange: (Int, Double) -> Unit,
    onRemove: (Int) -> Unit,
    onDiscount: (Double) -> Unit,
    onClear: () -> Unit,
    onComplete: (Double) -> Unit
) {
    var received by remember { mutableStateOf("") }
    val receivedValue = received.toDoubleOrNull() ?: 0.0
    val total = summary.total
    val change = (receivedValue - total).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("< Products") }
            Text("Cart", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onClear) { Text("Clear") }
        }
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))

        cart.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.productName, fontWeight = FontWeight.Bold)
                    if (item.variantName != null) {
                        Text(item.variantName, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "%.2f x %.2f = %.2f".format(item.unitPrice, item.quantity, item.lineSubtotal),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (item.isWeightBased) {
                    OutlinedTextField(
                        value = item.quantity.toString(),
                        onValueChange = { onQuantityChange(index, it.toDoubleOrNull() ?: 0.0) },
                        modifier = Modifier.width(90.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                } else {
                    Row {
                        TextButton(onClick = { onQuantityChange(index, item.quantity - 1) }) { Text("-") }
                        Text("${item.quantity.toInt()}", modifier = Modifier.align(Alignment.CenterVertically))
                        TextButton(onClick = { onQuantityChange(index, item.quantity + 1) }) { Text("+") }
                    }
                }
                IconButton(onClick = { onRemove(index) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remove")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
        }

        Spacer(Modifier.height(8.dp))

        CustomerDropdown(customers, selectedCustomer, onSelectCustomer)

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = summary.discount.toString(),
            onValueChange = { onDiscount(it.toDoubleOrNull() ?: 0.0) },
            label = { Text("Cart discount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(Modifier.height(12.dp))

        Text("Subtotal:  $%.2f".format(summary.subtotal))
        Text("Discount:  $%.2f".format(summary.discount))
        Text("Tax:       $%.2f".format(summary.tax))
        Text(
            "Total:     $%.2f".format(total),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = received,
            onValueChange = { received = it },
            label = { Text("Amount received (cash)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        if (receivedValue > 0) {
            Text("Change due: $%.2f".format(change), style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onComplete(receivedValue) },
            enabled = cart.isNotEmpty() && receivedValue >= total,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Complete Sale — Total $%.2f".format(total))
        }
        if (cart.isNotEmpty() && receivedValue >= 1.0 && receivedValue < total && total > 0) {            Text(
                "Amount received is less than total. Enter the full amount to complete.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun CustomerDropdown(
    customers: List<CustomerEntity>,
    selected: CustomerEntity?,
    onSelect: (CustomerEntity?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selected?.name ?: "Walk-in Customer",
            onValueChange = {},
            readOnly = true,
            label = { Text("Customer") },
            trailingIcon = { androidx.compose.material3.Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Walk-in Customer") },
                onClick = { onSelect(null); expanded = false }
            )
            customers.filter { !it.isWalkIn }.forEach { c ->
                DropdownMenuItem(
                    text = { Text(c.name) },
                    onClick = { onSelect(c); expanded = false }
                )
            }
        }
    }
}
