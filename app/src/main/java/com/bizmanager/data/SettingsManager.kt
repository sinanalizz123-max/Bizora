package com.bizmanager.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val MODULE_SELLING = booleanPreferencesKey("module_selling")
        val MODULE_INVENTORY = booleanPreferencesKey("module_inventory")
        val MODULE_CUSTOMERS = booleanPreferencesKey("module_customers")
        val MODULE_EXPENSES = booleanPreferencesKey("module_expenses")
        val MODULE_REPORTS = booleanPreferencesKey("module_reports")
        val TAX_PREFERENCE = stringPreferencesKey("tax_preference")
        val INVENTORY_BEHAVIOR = stringPreferencesKey("inventory_behavior")
    }

    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }
    val sellingEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.MODULE_SELLING] ?: true }
    val inventoryEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.MODULE_INVENTORY] ?: false }
    val customersEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.MODULE_CUSTOMERS] ?: false }
    val expensesEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.MODULE_EXPENSES] ?: false }
    val reportsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.MODULE_REPORTS] ?: true }

    val settingsChanged: Flow<Int> = context.dataStore.data.map { data ->
        var hash = 0
        hash = 31 * hash + (data[Keys.ONBOARDING_DONE]?.hashCode() ?: 0)
        hash = 31 * hash + (data[Keys.MODULE_SELLING]?.hashCode() ?: 0)
        hash = 31 * hash + (data[Keys.MODULE_INVENTORY]?.hashCode() ?: 0)
        hash = 31 * hash + (data[Keys.MODULE_CUSTOMERS]?.hashCode() ?: 0)
        hash = 31 * hash + (data[Keys.MODULE_EXPENSES]?.hashCode() ?: 0)
        hash
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    }

    suspend fun setSelling(enabled: Boolean) {
        context.dataStore.edit { it[Keys.MODULE_SELLING] = enabled }
    }

    suspend fun setInventory(enabled: Boolean) {
        context.dataStore.edit { it[Keys.MODULE_INVENTORY] = enabled }
    }

    suspend fun setCustomers(enabled: Boolean) {
        context.dataStore.edit { it[Keys.MODULE_CUSTOMERS] = enabled }
    }

    suspend fun setExpenses(enabled: Boolean) {
        context.dataStore.edit { it[Keys.MODULE_EXPENSES] = enabled }
    }

    suspend fun setReports(enabled: Boolean) {
        context.dataStore.edit { it[Keys.MODULE_REPORTS] = enabled }
    }

    suspend fun setTaxPreference(value: String) {
        context.dataStore.edit { it[Keys.TAX_PREFERENCE] = value }
    }
}
