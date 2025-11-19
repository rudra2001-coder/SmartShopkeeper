
package com.rudra.smartshopkeeper.data.repositories

import com.rudra.smartshopkeeper.data.database.daos.ProductDao
import com.rudra.smartshopkeeper.data.database.daos.SaleDao
import com.rudra.smartshopkeeper.data.database.entities.Sale
import com.rudra.smartshopkeeper.data.database.entities.SaleItem
import com.rudra.smartshopkeeper.domain.repositories.SaleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaleRepositoryImpl @Inject constructor(
    private val saleDao: SaleDao,
    private val productDao: ProductDao
) : SaleRepository {
    override suspend fun insertSale(sale: Sale): Long = saleDao.insertSale(sale)
    override suspend fun insertSaleItem(saleItem: SaleItem) = saleDao.insertSaleItem(saleItem)
    override fun getAllSales(): Flow<List<Sale>> = saleDao.getAllSales()
    override fun getSalesBetweenDates(startDate: Long, endDate: Long): Flow<List<Sale>> = saleDao.getSalesBetweenDates(startDate, endDate)
    override suspend fun getSaleItemsBetweenDates(startDate: Long, endDate: Long): List<SaleItem> = saleDao.getSaleItemsBetweenDates(startDate, endDate)
    override suspend fun getTotalSalesBetweenDates(startDate: Long, endDate: Long): Double? = saleDao.getTotalSalesBetweenDates(startDate, endDate)
    override suspend fun getTotalPaidBetweenDates(startDate: Long, endDate: Long): Double? = saleDao.getTotalPaidBetweenDates(startDate, endDate)

    override suspend fun getTotalCostOfGoodsSold(startDate: Long, endDate: Long): Double? {
        val saleItems = getSaleItemsBetweenDates(startDate, endDate)
        var totalCost = 0.0
        for (item in saleItems) {
            val product = productDao.getProductById(item.productId)
            if (product != null) {
                totalCost += product.costPrice * item.quantity
            }
        }
        return totalCost
    }

    override suspend fun getSaleItems(saleId: Long): List<SaleItem> = saleDao.getSaleItems(saleId)
    override fun searchSales(invoiceNumber: String): Flow<List<Sale>> = saleDao.searchSales(invoiceNumber)
    override fun getSalesForCustomer(customerId: String): Flow<List<Sale>> = saleDao.getSalesForCustomer(customerId)

    override suspend fun getProfitPerProduct(startDate: Long, endDate: Long): Map<String, Double> {
        val saleItems = getSaleItemsBetweenDates(startDate, endDate)
        val profitMap = mutableMapOf<String, Double>()

        for (item in saleItems) {
            val product = productDao.getProductById(item.productId)
            if (product != null) {
                val profit = (item.unitPrice - product.costPrice) * item.quantity
                profitMap[product.name] = (profitMap[product.name] ?: 0.0) + profit
            }
        }
        return profitMap
    }

    override suspend fun getProfitPerCategory(startDate: Long, endDate: Long): Map<String, Double> {
        val saleItems = getSaleItemsBetweenDates(startDate, endDate)
        val profitMap = mutableMapOf<String, Double>()

        for (item in saleItems) {
            val product = productDao.getProductById(item.productId)
            if (product != null) {
                val profit = (item.unitPrice - product.costPrice) * item.quantity
                profitMap[product.category] = (profitMap[product.category] ?: 0.0) + profit
            }
        }
        return profitMap
    }
}
