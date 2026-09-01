package com.bizmanager.data.repository

import com.bizmanager.data.dao.CategoryDao
import com.bizmanager.data.dao.ProductDao
import com.bizmanager.data.dao.ProductVariantDao
import com.bizmanager.data.entity.CategoryEntity
import com.bizmanager.data.entity.ProductEntity
import com.bizmanager.data.entity.ProductVariantEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao,
    private val variantDao: ProductVariantDao
) {
    val categories: Flow<List<CategoryEntity>> = categoryDao.observeAll()
    val products: Flow<List<ProductEntity>> = productDao.observeActive()

    fun productsByCategory(categoryId: Long): Flow<List<ProductEntity>> =
        productDao.observeByCategory(categoryId)

    fun favorites(): Flow<List<ProductEntity>> = productDao.observeFavorites()

    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.search(query)

    fun variantsForProduct(productId: Long): Flow<List<ProductVariantEntity>> =
        variantDao.observeForProduct(productId)

    suspend fun getVariants(productId: Long): List<ProductVariantEntity> =
        variantDao.getForProduct(productId)

    suspend fun getProduct(id: Long): ProductEntity? = productDao.getById(id)

    suspend fun addCategory(name: String, sortOrder: Int = 0): Long =
        categoryDao.insert(CategoryEntity(name = name, sortOrder = sortOrder))

    suspend fun addProduct(product: ProductEntity): Long = productDao.insert(product)

    suspend fun updateProduct(product: ProductEntity) = productDao.update(product)

    suspend fun addVariant(variant: ProductVariantEntity): Long = variantDao.insert(variant)

    suspend fun deleteVariantsForProduct(productId: Long) = variantDao.deleteForProduct(productId)

    suspend fun archiveProduct(id: Long) = productDao.archive(id)

    suspend fun getAllCategories(): List<CategoryEntity> = categoryDao.getAll()

    suspend fun clearCategories() = categoryDao.deleteAll()
}
