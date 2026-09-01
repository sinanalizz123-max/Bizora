package com.bizmanager.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bizmanager.MainViewModel
import com.bizmanager.data.entity.BusinessEntity
import com.bizmanager.data.entity.CategoryEntity
import com.bizmanager.data.entity.ProductEntity
import com.bizmanager.data.entity.ProductVariantEntity
import com.bizmanager.ui.onboarding.templates.BusinessTemplates
import kotlinx.coroutines.launch

@Composable
fun OnboardingFlow(viewModel: MainViewModel) {
    var step by remember { mutableStateOf(1) }

    val scope = rememberCoroutineScope()
    var businessName by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("₹") }
    var businessType by remember { mutableStateOf("restaurant") }
    var selectedTemplates by remember { mutableStateOf(mutableMapOf<String, List<String>>()) }
    var customCategory by remember { mutableStateOf("") }
    var customProduct by remember { mutableStateOf("") }
    var customProductPrice by remember { mutableStateOf("") }
    var sellMod by remember { mutableStateOf(true) }
    var invMod by remember { mutableStateOf(false) }
    var custMod by remember { mutableStateOf(false) }
    var expMod by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (step) {
            1 -> WelcomeStep(onNext = { step = 2 })
            2 -> BusinessInfoStep(
                businessName = businessName,
                onNameChange = { businessName = it },
                currency = currency,
                onCurrencyChange = { currency = it },
                onNext = { step = 3 },
                onBack = { step = 1 }
            )
            3 -> BusinessTypeStep(
                selected = businessType,
                onSelect = { businessType = it },
                onNext = { step = 4 },
                onBack = { step = 2 }
            )
            4 -> TemplateStep(
                businessType = businessType,
                selected = selectedTemplates,
                onToggle = { cat, prod ->
                    val list = selectedTemplates.getOrPut(cat) { mutableListOf() }.toMutableList()
                    if (prod in list) list.remove(prod) else list.add(prod)
                    selectedTemplates[cat] = list
                },
                customCategory = customCategory,
                onCustomCategoryChange = { customCategory = it },
                customProduct = customProduct,
                onCustomProductChange = { customProduct = it },
                customProductPrice = customProductPrice,
                onCustomPriceChange = { customProductPrice = it },
                onAddCustom = {
                    if (customCategory.isNotBlank() && customProduct.isNotBlank()) {
                        val list = selectedTemplates.getOrPut(customCategory) { mutableListOf() }.toMutableList()
                        list.add(customProduct + (if (customProductPrice.isNotBlank()) "@${customProductPrice}" else ""))
                        selectedTemplates[customCategory] = list
                        customCategory = ""
                        customProduct = ""
                        customProductPrice = ""
                    }
                },
                onNext = {
                    scope.launch {
                        viewModel.productRepository.clearCategories()
                        selectedTemplates.forEach { (catName, products) ->
                            val catId = viewModel.productRepository.addCategory(catName)
                            products.forEach { raw ->
                                val (name, priceStr) = parseProduct(raw)
                                val price = priceStr?.toDoubleOrNull() ?: 0.0
                                viewModel.productRepository.addProduct(
                                    ProductEntity(name = name, categoryId = catId, price = price)
                                )
                            }
                        }
                        step = 5
                    }
                },
                onSkip = {
                    scope.launch { viewModel.productRepository.clearCategories(); step = 5 }
                },
                onBack = { step = 3 }
            )
            5 -> FeatureStep(
                sell = sellMod, onSell = { sellMod = it },
                inv = invMod, onInv = { invMod = it },
                cust = custMod, onCust = { custMod = it },
                exp = expMod, onExp = { expMod = it },
                onBack = { step = 4 },
                onNext = { step = 6 }
            )
            6 -> FinishStep(
                vm = viewModel,
                businessName = businessName,
                currency = currency,
                businessType = businessType,
                sell = sellMod,
                inv = invMod,
                cust = custMod,
                exp = expMod
            )
        }
    }
}

private fun parseProduct(raw: String): Pair<String, String?> {
    val at = raw.lastIndexOf('@')
    return if (at > 0) {
        raw.substring(0, at) to raw.substring(at + 1)
    } else raw to null
}
