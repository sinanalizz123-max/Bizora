package com.bizmanager.ui.onboarding

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bizmanager.ui.onboarding.templates.BusinessTemplates

@Composable
fun TemplateStep(
    businessType: String,
    selected: Map<String, List<String>>,
    onToggle: (String, String) -> Unit,
    customCategory: String,
    onCustomCategoryChange: (String) -> Unit,
    customProduct: String,
    onCustomProductChange: (String) -> Unit,
    customProductPrice: String,
    onCustomPriceChange: (String) -> Unit,
    onAddCustom: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    val categories = BusinessTemplates.categoriesFor(businessType)
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Starter Items", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(
                "Pick categories and items to start with. These are just suggestions — you can edit or delete them anytime.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { (catName, products) ->
                CategoryCard(
                    catName = catName,
                    products = products,
                    selected = selected[catName].orEmpty(),
                    onToggle = onToggle
                )
            }
            item {
                Spacer(Modifier.height(8.dp))
                Text("Add your own", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = customCategory,
                    onValueChange = onCustomCategoryChange,
                    label = { Text("Category name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = customProduct,
                    onValueChange = onCustomProductChange,
                    label = { Text("Product name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = customProductPrice,
                    onValueChange = onCustomPriceChange,
                    label = { Text("Price (optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onAddCustom, modifier = Modifier.fillMaxWidth()) {
                    Text("Add this product")
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Back") }
            OutlinedButton(onClick = onSkip, modifier = Modifier.weight(1f)) { Text("Skip") }
            Button(onClick = onNext, modifier = Modifier.weight(1f)) { Text("Next") }
        }
    }
}

@Composable
private fun CategoryCard(
    catName: String,
    products: List<BusinessTemplates.TemplateProduct>,
    selected: List<String>,
    onToggle: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(catName, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        products.forEach { p ->
            val key = "${p.name}@${p.price}"
            val isSel = key in selected
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(catName, key) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isSel, onCheckedChange = { onToggle(catName, key) })
                Text(p.name, modifier = Modifier.weight(1f))
                Text("$" + "%.2f".format(p.price))
            }
        }
    }
}
