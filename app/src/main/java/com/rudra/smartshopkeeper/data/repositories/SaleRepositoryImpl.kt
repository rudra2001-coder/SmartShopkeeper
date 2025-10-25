
package com.rudra.smartshopkeeper.data.repositories

import com.rudra.smartshopkeeper.data.database.daos.SaleDao
import com.rudra.smartshopkeeper.data.database.entities.Sale
import com.rudra.smartshopkeeper.data.database.entities.SaleItem
import com.rudra.smartshopkeeper.domain.repositories.SaleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaleRepositoryImpl @Inject constructor(
    private val saleDao: SaleDao
) : SaleRepository {
    override suspend fun insertSale(sale: Sale): Long = saleDao.insertSale(sale)
    override suspend fun insertSaleItem(saleItem: SaleItem) = saleDao.insertSaleItem(saleItem)
    override fun getAllSales(): Flow<List<Sale>> = saleDao.getAllSales()
    override fun getSalesBetweenDates(startDate: Long, endDate: Long): Flow<List<Sale>> = saleDao.getSalesBetweenDates(startDate, endDate)
    override suspend fun getSaleItemsBetweenDates(startDate: Long, endDate: Long): List<SaleItem> = saleDao.getSaleItemsBetweenDates(startDate, endDate)
    override suspend fun getTotalSalesBetweenDates(startDate: Long, endDate: Long): Double? = saleDao.getTotalSalesBetweenDates(startDate, endDate)
    override suspend fun getTotalPaidBetweenDates(startDate: Long, endDate: Long): Double? = saleDao.getTotalPaidBetweenDates(startDate, endDate)
}
