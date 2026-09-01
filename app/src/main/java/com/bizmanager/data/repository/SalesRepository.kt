package com.bizmanager.data.repository

import com.bizmanager.data.dao.CustomerDao
import com.bizmanager.data.dao.RefundDao
import com.bizmanager.data.dao.SaleDao
import com.bizmanager.data.entity.CustomerEntity
import com.bizmanager.data.entity.RefundEntity
import com.bizmanager.data.entity.SaleEntity
import com.bizmanager.data.entity.SaleItemEntity
import kotlinx.coroutines.flow.Flow

class SalesRepository(
    private val saleDao: SaleDao,
    private val refundDao: RefundDao,
    private val customerDao: CustomerDao
) {
    val sales: Flow<List<SaleEntity>> = saleDao.observeAll()
    val customers: Flow<List<CustomerEntity>> = customerDao.observeAll()

    fun salesBetween(start: Long, end: Long): Flow<List<SaleEntity>> =
        saleDao.observeBetween(start, end)

    suspend fun recordSale(sale: SaleEntity, items: List<SaleItemEntity>): Long {
        val id = saleDao.insertSale(sale)
        saleDao.insertItems(items.map { it.copy(saleId = id) })
        return id
    }

    suspend fun getSale(id: Long): SaleEntity? = saleDao.getById(id)
    suspend fun getSaleItems(id: Long): List<SaleItemEntity> = saleDao.getItems(id)
    suspend fun nextTransactionNumber(): String {
        val max = saleDao.getMaxId() ?: 0L
        return "INV-%06d".format(max + 1)
    }

    suspend fun addRefund(refund: RefundEntity): Long = refundDao.insert(refund)
    fun refundsForSale(saleId: Long): Flow<List<RefundEntity>> = refundDao.observeForSale(saleId)
    val refunds: Flow<List<RefundEntity>> = refundDao.observeAll()

    fun searchCustomers(query: String) = customerDao.search(query)
    suspend fun addCustomer(customer: CustomerEntity): Long = customerDao.insert(customer)
    suspend fun getWalkInCustomer(): CustomerEntity? = customerDao.getWalkIn()
    suspend fun getCustomer(id: Long): CustomerEntity? = customerDao.getById(id)
}
