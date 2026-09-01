package com.bizmanager.data.repository

import com.bizmanager.data.dao.InventoryDao
import com.bizmanager.data.dao.StockMovementDao
import com.bizmanager.data.dao.ProductDao
import com.bizmanager.data.entity.InventoryEntity
import com.bizmanager.data.entity.StockMovementEntity
import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val inventoryDao: InventoryDao,
    private val stockMovementDao: StockMovementDao,
    private val productDao: ProductDao
) {
    val inventory: Flow<List<InventoryEntity>> = inventoryDao.observeAll()
    val lowStock: Flow<List<InventoryEntity>> = inventoryDao.observeLowStock()

    fun movementsForProduct(productId: Long): Flow<List<StockMovementEntity>> =
        stockMovementDao.observeForProduct(productId)

    suspend fun getInventoryForProduct(productId: Long): InventoryEntity? =
        inventoryDao.getForProduct(productId)

    suspend fun addStock(productId: Long, qty: Double, note: String? = null) {
        val current = inventoryDao.getForProduct(productId)
        inventoryDao.upsert(
            current?.copy(quantity = current.quantity + qty)
                ?: InventoryEntity(productId = productId, quantity = qty)
        )
        stockMovementDao.insert(
            StockMovementEntity(productId = productId, type = "add", quantity = qty, notes = note)
        )
    }

    suspend fun removeStock(productId: Long, qty: Double, note: String? = null) {
        val current = inventoryDao.getForProduct(productId)
        if (current != null) {
            inventoryDao.upsert(current.copy(quantity = (current.quantity - qty).coerceAtLeast(0.0)))
            stockMovementDao.insert(
                StockMovementEntity(productId = productId, type = "remove", quantity = qty, notes = note)
            )
        }
    }
}
