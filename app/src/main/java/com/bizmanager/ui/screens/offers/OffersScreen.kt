package com.bizmanager.ui.screens.offers

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import com.bizmanager.data.entity.OfferEntity
import kotlinx.coroutines.launch

@Composable
fun OffersScreen(mainViewModel: MainViewModel) {
    val app = mainViewModel.getApplication<BusinessManagerApp>()
    val vm: OffersViewModel = viewModel(factory = OffersVMFactory(app))
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val offers by vm.offers.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add offer")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                "Offers & Discounts",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(12.dp)
            )
            if (offers.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No offers yet")
                    Text("Create percentage or flat discounts with the + button.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(offers, key = { it.id }) { offer ->
                        OfferCard(
                            offer = offer,
                            onToggle = {
                                scope.launch {
                                    vm.toggleActive(offer)
                                    Toast.makeText(context, if (offer.isActive) "Offer deactivated" else "Offer activated", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDelete = {
                                scope.launch {
                                    vm.deleteOffer(offer)
                                    Toast.makeText(context, "Offer deleted", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddOfferDialog(
            onDismiss = { showAdd = false },
            onSave = { name, type, value ->
                scope.launch {
                    vm.addOffer(OfferEntity(name = name, type = type, value = value))
                    Toast.makeText(context, "Offer added", Toast.LENGTH_SHORT).show()
                    showAdd = false
                }
            }
        )
    }
}

@Composable
private fun OfferCard(offer: OfferEntity, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    offer.name,
                    fontWeight = FontWeight.Bold,
                    color = if (offer.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    if (offer.type == OfferType.PERCENTAGE) "${offer.value}% off" else "$%.2f off".format(offer.value),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    if (offer.isActive) "Active" else "Inactive",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (offer.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            Switch(checked = offer.isActive, onCheckedChange = { onToggle() })
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete offer")
            }
        }
    }
}

@Composable
private fun AddOfferDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(OfferType.PERCENTAGE) }
    var value by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Offer") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { type = OfferType.PERCENTAGE }) {
                        Text(
                            "Percentage",
                            fontWeight = if (type == OfferType.PERCENTAGE) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    TextButton(onClick = { type = OfferType.FLAT }) {
                        Text(
                            "Flat Amount",
                            fontWeight = if (type == OfferType.FLAT) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(if (type == OfferType.PERCENTAGE) "Percent %" else "Amount $") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { value.toDoubleOrNull()?.let { onSave(name.trim(), type, it) } },
                enabled = name.isNotBlank() && value.toDoubleOrNull() != null
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
