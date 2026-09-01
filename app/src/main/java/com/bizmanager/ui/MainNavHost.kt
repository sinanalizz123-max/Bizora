package com.bizmanager.ui

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bizmanager.BusinessManagerApp
import com.bizmanager.MainViewModel
import com.bizmanager.R
import com.bizmanager.ui.screens.customers.CustomersScreen
import com.bizmanager.ui.screens.dashboard.DashboardScreen
import com.bizmanager.ui.screens.cashregister.CashRegisterScreen
import com.bizmanager.ui.screens.expenses.ExpensesScreen
import com.bizmanager.ui.screens.inventory.InventoryScreen
import com.bizmanager.ui.screens.offers.OffersScreen
import com.bizmanager.ui.screens.products.ProductsScreen
import com.bizmanager.ui.screens.sales.SalesScreen
import com.bizmanager.ui.screens.settings.SettingsScreen
import com.bizmanager.ui.screens.selling.SellingScreen
import com.bizmanager.ui.screens.tax.TaxScreen

object Routes {
    const val DASHBOARD = "dashboard"
    const val SELLING = "selling"
    const val PRODUCTS = "products"
    const val SALES = "sales"
    const val INVENTORY = "inventory"
    const val CUSTOMERS = "customers"
    const val EXPENSES = "expenses"
    const val OFFERS = "offers"
    const val TAX = "tax"
    const val CASH_REGISTER = "cash_register"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
}

data class NavItem(
    val route: String,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun MainNavHost(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val sellingEnabled by viewModel.sellingEnabled.collectAsState()
    val inventoryEnabled by viewModel.inventoryEnabled.collectAsState()
    val customersEnabled by viewModel.customersEnabled.collectAsState()
    val expensesEnabled by viewModel.expensesEnabled.collectAsState()

    val bottomItems = buildList {
        add(NavItem(Routes.SELLING, R.string.nav_selling, Icons.Filled.PointOfSale))
        add(NavItem(Routes.PRODUCTS, R.string.nav_products, Icons.Filled.Category))
        if (inventoryEnabled) add(NavItem(Routes.INVENTORY, R.string.nav_inventory, Icons.Filled.Inventory))
        add(NavItem(Routes.SALES, R.string.nav_sales, Icons.Filled.Receipt))
        if (customersEnabled) add(NavItem(Routes.CUSTOMERS, R.string.nav_customers, Icons.Filled.AccountCircle))
        if (expensesEnabled) add(NavItem(Routes.EXPENSES, R.string.nav_expenses, Icons.Filled.ShoppingCart))
        add(NavItem(Routes.SETTINGS, R.string.nav_settings, Icons.Filled.Settings))
    }

    val startRoute = if (sellingEnabled) Routes.SELLING else Routes.DASHBOARD

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(stringResource(item.labelRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) { DashboardScreen(navController) }
            composable(Routes.SELLING) { SellingScreen(navController, viewModel) }
            composable(Routes.PRODUCTS) { ProductsScreen(navController, viewModel) }
            composable(Routes.SALES) { SalesScreen(navController, viewModel) }
            composable(Routes.INVENTORY) { InventoryScreen(viewModel) }
            composable(Routes.CUSTOMERS) { CustomersScreen(viewModel) }
            composable(Routes.EXPENSES) { ExpensesScreen(viewModel) }
            composable(Routes.OFFERS) { OffersScreen(viewModel) }
            composable(Routes.TAX) { TaxScreen(viewModel) }
            composable(Routes.CASH_REGISTER) { CashRegisterScreen(viewModel) }
            composable(Routes.SETTINGS) { SettingsScreen(navController, viewModel) }
        }
    }
}
