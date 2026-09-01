package com.bizmanager.data

import android.content.Context
import androidx.room.Room
import com.bizmanager.data.repository.BusinessRepository
import com.bizmanager.data.repository.ExpenseRepository
import com.bizmanager.data.repository.InventoryRepository
import com.bizmanager.data.repository.ProductRepository
import com.bizmanager.data.repository.SalesRepository

class AppContainer(private val context: Context) {
    private val database = AppDatabase.getInstance(context)
    val settings = SettingsManager(context)

    val businessRepository = BusinessRepository(database.businessDao())
    val productRepository = ProductRepository(
        database.categoryDao(),
        database.productDao(),
        database.productVariantDao()
    )
    val salesRepository = SalesRepository(
        database.saleDao(),
        database.refundDao(),
        database.customerDao()
    )
    val expenseRepository = ExpenseRepository(database.expenseDao())
    val inventoryRepository = InventoryRepository(
        database.inventoryDao(),
        database.stockMovementDao(),
        database.productDao()
    )
}
