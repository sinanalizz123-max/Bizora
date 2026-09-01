package com.bizmanager.ui.screens.selling

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bizmanager.data.entity.CategoryEntity
import com.bizmanager.data.entity.ProductEntity
import com.bizmanager.data.entity.ProductVariantEntity
import com.bizmanager.ui.components.productCard

@Composable
fun ProductGrid(
    cartCount: Int,
    total: Double,
    onCheckout: () -> Unit,
    categories: List<CategoryEntity>,
    selectedCategory: Long?,
    onSelectCategory: (Long?) -> Unit,
    search: String,
    onSearch: (String) -> Unit,
    products: List<Pair<ProductEntity, List<ProductVariantEntity>>>,
    onAdd: (ProductEntity) -> Unit,
    onAddVariant: (ProductEntity, ProductVariantEntity) -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = search,
                    onValueChange = onSearch,
                    placeholder = { Text("Search products") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.height(56.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat.id,
                            onClick = { onSelectCategory(if (selectedCategory == cat.id) null else cat.id) },
                            label = { Text(cat.name) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Cart", style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                    Text("$" + "%.2f".format(total), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Button(onClick = onCheckout) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                    Text("  Checkout ($cartCount)")
                }
            }
        }
    ) { padding ->
        if (products.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No products yet")
                Spacer(Modifier.height(8.dp))
                Text(
                    "Add products from the Products screen, or finish setup with starter items.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { (product, variants) ->
                    productCard(
                        product = product,
                        variants = variants,
                        onAdd = { onAdd(product) },
                        onAddVariant = { onAddVariant(product, it) }
                    )
                }
            }
        }
    }
}
