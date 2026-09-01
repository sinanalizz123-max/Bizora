package com.bizmanager.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bizmanager.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    val scope = rememberCoroutineScope()
    val selling by mainViewModel.sellingEnabled.collectAsState()
    val inventory by mainViewModel.inventoryEnabled.collectAsState()
    val customers by mainViewModel.customersEnabled.collectAsState()
    val expenses by mainViewModel.expensesEnabled.collectAsState()
    val reports by mainViewModel.reportsEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Features", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                SettingSwitch("Selling / POS", "Selling screen for your counter", selling) {
                    scope.launch { mainViewModel.settings.setSelling(it) }
                }
                SettingSwitch("Inventory", "Track stock and movements", inventory) {
                    scope.launch { mainViewModel.settings.setInventory(it) }
                }
                SettingSwitch("Customers", "Manage customers", customers) {
                    scope.launch { mainViewModel.settings.setCustomers(it) }
                }
                SettingSwitch("Expenses", "Track business expenses", expenses) {
                    scope.launch { mainViewModel.settings.setExpenses(it) }
                }
                SettingSwitch("Reports", "Business reports", reports) {
                    scope.launch { mainViewModel.settings.setReports(it) }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Data", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = { navController.navigate(com.bizmanager.ui.Routes.OFFERS) }) {
                    Text("Offers & Discounts")
                }
                TextButton(onClick = { navController.navigate(com.bizmanager.ui.Routes.TAX) }) {
                    Text("Tax Rates")
                }
                TextButton(onClick = { navController.navigate(com.bizmanager.ui.Routes.CASH_REGISTER) }) {
                    Text("Cash Register")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Card {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Setup", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = {
                    scope.launch { mainViewModel.settings.setOnboardingDone(false) }
                }) {
                    Text("Re-run onboarding / reset setup")
                }
            }
        }
    }
}

@Composable
private fun SettingSwitch(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
