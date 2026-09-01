package com.bizmanager.ui.screens.products

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.navigation.NavHostController
import com.bizmanager.BusinessManagerApp
import com.bizmanager.MainViewModel
import com.bizmanager.data.entity.CategoryEntity
import com.bizmanager.data.entity.ProductEntity
import com.bizmanager.data.entity.ProductVariantEntity
import kotlinx.coroutines.launch

@Composable
fun ProductsScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    val app = mainViewModel.getApplication<BusinessManagerApp>()
    val vm: ProductsViewModel = viewModel(factory = ProductsVMFactory(app))
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val categories by vm.categories.collectAsState()
    val products by vm.products.collectAsState()
    var selectedCategory by remember { mutableStateOf<Long?>(null) }
    var showAdd by remember { mutableStateOf(false) }

    val filtered = if (selectedCategory == null) products else products.filter { it.categoryId == selectedCategory }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add product")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All") }
                )
                categories.forEach { c ->
                    FilterChip(
                        selected = selectedCategory == c.id,
                        onClick = { selectedCategory = c.id },
                        label = { Text(c.name) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            if (filtered.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No products here yet")
                    Text("Tap + to add your first product.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { product ->
                        val catName = categories.firstOrNull { it.id == product.categoryId }?.name
                        Card {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, fontWeight = FontWeight.Bold)
                                    if (catName != null || product.isWeightBased) {
                                        Text(
                                            listOfNotNull(catName, if (product.isWeightBased) "by weight" else null).joinToString(" · "),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "$%.2f".format(product.price),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (product.taxRate > 0) {
                                        Text("${product.taxRate}% tax", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddProductDialog(
            categories = categories,
            onDismiss = { showAdd = false },
            onSave = { product, variantNames, variantPrices ->
                scope.launch {
                    val pid = vm.addProduct(product)
                    variantNames.forEachIndexed { i, vn ->
                        if (vn.isNotBlank()) {
                            vm.addVariant(
                                ProductVariantEntity(
                                    productId = pid,
                                    name = vn,
                                    price = variantPrices.getOrElse(i) { "" }.toDoubleOrNull() ?: product.price
                                )
                            )
                        }
                    }
                    Toast.makeText(context, "Product added", Toast.LENGTH_SHORT).show()
                    showAdd = false
                }
            }
        )
    }
}

@Composable
private fun AddProductDialog(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (ProductEntity, List<String>, List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var weightBased by remember { mutableStateOf(false) }
    var tax by remember { mutableStateOf("0") }
    var selectedCategory by remember { mutableStateOf<Long?>(null) }
    var variantCount by remember { mutableStateOf(0) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    val variantNames = remember { mutableListOf("", "", "", "") }
    val variantPrices = remember { mutableListOf("", "", "", "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Product") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = tax,
                    onValueChange = { tax = it },
                    label = { Text("Tax rate %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = weightBased, onCheckedChange = { weightBased = it })
                    Text("Sold by weight (e.g. kg)")
                }
                TextButton(onClick = { showCategoryPicker = !showCategoryPicker }) {
                    Text("Category: ${categories.firstOrNull { it.id == selectedCategory }?.name ?: "None"} ▾")
                }
                if (showCategoryPicker) {
                    categories.forEach { c ->
                        TextButton(onClick = { selectedCategory = c.id; showCategoryPicker = false }) {
                            Text(c.name)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text("Variants (optional)", fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { if (variantCount < 4) variantCount++ }, enabled = variantCount < 4) {
                        Text("+ Add variant")
                    }
                    if (variantCount > 0) {
                        TextButton(onClick = { variantCount-- }) { Text("Remove last") }
                    }
                }
                (0 until variantCount).forEach { i ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = variantNames[i],
                            onValueChange = { variantNames[i] = it },
                            label = { Text("Variant name") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = variantPrices[i],
                            onValueChange = { variantPrices[i] = it },
                            label = { Text("Price") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        ProductEntity(
                            name = name.trim(),
                            price = price.toDoubleOrNull() ?: 0.0,
                            categoryId = selectedCategory,
                            taxRate = tax.toDoubleOrNull() ?: 0.0,
                            isWeightBased = weightBased
                        ),
                        variantNames,
                        variantPrices
                    )
                },
                enabled = name.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
