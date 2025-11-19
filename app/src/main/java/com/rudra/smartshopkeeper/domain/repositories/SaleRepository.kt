
package com.rudra.smartshopkeeper.domain.repositories

import com.rudra.smartshopkeeper.data.database.entities.Sale
import com.rudra.smartshopkeeper.data.database.entities.SaleItem
import kotlinx.coroutines.flow.Flow

interface SaleRepository {
    suspend fun insertSale(sale: Sale): Long
    suspend fun insertSaleItem(saleItem: SaleItem)
    fun getAllSales(): Flow<List<Sale>>
    fun getSalesBetweenDates(startDate: Long, endDate: Long): Flow<List<Sale>>
    suspend fun getSaleItemsBetweenDates(startDate: Long, endDate: Long): List<SaleItem>
    suspend fun getTotalSalesBetweenDates(startDate: Long, endDate: Long): Double?
    suspend fun getTotalPaidBetweenDates(startDate: Long, endDate: Long): Double?
    suspend fun getTotalCostOfGoodsSold(startDate: Long, endDate: Long): Double?
    suspend fun getSaleItems(saleId: Long): List<SaleItem>
    fun searchSales(invoiceNumber: String): Flow<List<Sale>>
    fun getSalesForCustomer(customerId: String): Flow<List<Sale>>
    suspend fun getProfitPerProduct(startDate: Long, endDate: Long): Map<String, Double>
    suspend fun getProfitPerCategory(startDate: Long, endDate: Long): Map<String, Double>
}
