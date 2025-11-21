
package com.rudra.smartshopkeeper.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.smartshopkeeper.data.database.entities.Purchase
import com.rudra.smartshopkeeper.data.database.entities.PurchaseItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: Purchase): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchaseItem(purchaseItem: PurchaseItem)

    @Query("SELECT * FROM purchases WHERE supplierId = :supplierId ORDER BY date DESC")
    fun getPurchasesForSupplier(supplierId: String): Flow<List<Purchase>>
}
