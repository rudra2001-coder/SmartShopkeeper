
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
}
