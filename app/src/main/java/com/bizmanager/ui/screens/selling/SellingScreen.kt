package com.bizmanager.ui.screens.selling

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bizmanager.BusinessManagerApp
import com.bizmanager.MainViewModel
import com.bizmanager.data.entity.CategoryEntity
import com.bizmanager.data.entity.ProductEntity
import com.bizmanager.data.entity.ProductVariantEntity
import com.bizmanager.ui.components.productCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellingScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    val app = mainViewModel.getApplication<BusinessManagerApp>()
    val vm: SellingViewModel = viewModel(factory = SellingVMFactory(app))
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCart by remember { mutableIntStateOf(0) }

    val cart by vm.cart.collectAsState()
    val summary by vm.cartSummary.collectAsState()
    val categories by vm.categories.collectAsState()
    val products by vm.displayedProducts.collectAsState()
    val selectedCategory by vm.selectedCategory.collectAsState()
    val search by vm.searchQuery.collectAsState()
    val selectedCustomer by vm.selectedCustomer.collectAsState()
    val customers by vm.customers.collectAsState()

    when (showCart) {
        0 -> ProductGrid(
            cartCount = cart.size,
            total = summary.total,
            onCheckout = { showCart = 1 },
            categories = categories,
            selectedCategory = selectedCategory,
            onSelectCategory = vm::selectCategory,
            search = search,
            onSearch = vm::setSearch,
            products = products,
            onAdd = vm::addToCart,
            onAddVariant = vm::addVariantToCart
        )
        1 -> CartScreen(
            cart = cart,
            summary = summary,
            customers = customers,
            selectedCustomer = selectedCustomer,
            onSelectCustomer = vm::selectCustomer,
            onBack = { showCart = 0 },
            onQuantityChange = vm::setCartQuantity,
            onRemove = vm::removeFromCart,
            onDiscount = vm::setCartDiscount,
            onClear = vm::clearCart,
            onComplete = { onPaid ->
                scope.launch {
                    try {
                        mainViewModel.recordSale(
                            items = cart,
                            subtotal = summary.subtotal,
                            discount = summary.discount,
                            tax = summary.tax,
                            total = summary.total,
                            amountReceived = onPaid,
                            customerId = selectedCustomer?.id
                        )
                        Toast.makeText(context, "Sale recorded", Toast.LENGTH_SHORT).show()
                        vm.clearCart()
                        showCart = 0
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}
