
package com.rudra.smartshopkeeper.domain.repositories

import com.rudra.smartshopkeeper.data.database.entities.Purchase
import com.rudra.smartshopkeeper.data.database.entities.PurchaseItem
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {
    suspend fun insertPurchase(purchase: Purchase, purchaseItems: List<PurchaseItem>)
    fun getPurchasesForSupplier(supplierId: String): Flow<List<Purchase>>
}
