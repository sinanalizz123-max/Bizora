package com.bizmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bizmanager.data.dao.BusinessDao
import com.bizmanager.data.dao.CategoryDao
import com.bizmanager.data.dao.CustomerDao
import com.bizmanager.data.dao.ExpenseDao
import com.bizmanager.data.dao.InventoryDao
import com.bizmanager.data.dao.OfferDao
import com.bizmanager.data.dao.ProductDao
import com.bizmanager.data.dao.ProductVariantDao
import com.bizmanager.data.dao.RefundDao
import com.bizmanager.data.dao.RegisterDao
import com.bizmanager.data.dao.SaleDao
import com.bizmanager.data.dao.StockMovementDao
import com.bizmanager.data.dao.TaxDao
import com.bizmanager.data.entity.BusinessEntity
import com.bizmanager.data.entity.CashRegisterEntity
import com.bizmanager.data.entity.CategoryEntity
import com.bizmanager.data.entity.CustomerEntity
import com.bizmanager.data.entity.ExpenseEntity
import com.bizmanager.data.entity.InventoryEntity
import com.bizmanager.data.entity.OfferEntity
import com.bizmanager.data.entity.ProductEntity
import com.bizmanager.data.entity.ProductVariantEntity
import com.bizmanager.data.entity.RefundEntity
import com.bizmanager.data.entity.SaleEntity
import com.bizmanager.data.entity.SaleItemEntity
import com.bizmanager.data.entity.StockMovementEntity
import com.bizmanager.data.entity.TaxEntity

@Database(
    entities = [
        BusinessEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        ProductVariantEntity::class,
        TaxEntity::class,
        CustomerEntity::class,
        SaleEntity::class,
        SaleItemEntity::class,
        RefundEntity::class,
        ExpenseEntity::class,
        OfferEntity::class,
        InventoryEntity::class,
        StockMovementEntity::class,
        CashRegisterEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun productVariantDao(): ProductVariantDao
    abstract fun customerDao(): CustomerDao
    abstract fun saleDao(): SaleDao
    abstract fun refundDao(): RefundDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun offerDao(): OfferDao
    abstract fun registerDao(): RegisterDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun taxDao(): TaxDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "business_manager.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
