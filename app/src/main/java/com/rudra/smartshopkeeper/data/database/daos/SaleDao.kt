
package com.rudra.smartshopkeeper.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.smartshopkeeper.data.database.entities.Sale
import com.rudra.smartshopkeeper.data.database.entities.SaleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItem(saleItem: SaleItem)
    
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<Sale>>
    
    @Query("SELECT * FROM sales WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getSalesBetweenDates(startDate: Long, endDate: Long): Flow<List<Sale>>
    
    @Query("SELECT si.* FROM sale_items si INNER JOIN sales s ON si.saleId = s.id WHERE s.date BETWEEN :startDate AND :endDate")
    suspend fun getSaleItemsBetweenDates(startDate: Long, endDate: Long): List<SaleItem>
    
    @Query("SELECT SUM(totalAmount) FROM sales WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalSalesBetweenDates(startDate: Long, endDate: Long): Double?
    
    @Query("SELECT SUM(paidAmount) FROM sales WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalPaidBetweenDates(startDate: Long, endDate: Long): Double?
}
