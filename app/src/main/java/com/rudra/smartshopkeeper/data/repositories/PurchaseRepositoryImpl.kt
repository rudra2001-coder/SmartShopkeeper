
package com.rudra.smartshopkeeper.data.repositories

import com.rudra.smartshopkeeper.data.database.daos.PurchaseDao
import com.rudra.smartshopkeeper.data.database.entities.Purchase
import com.rudra.smartshopkeeper.data.database.entities.PurchaseItem
import com.rudra.smartshopkeeper.domain.repositories.PurchaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PurchaseRepositoryImpl @Inject constructor(
    private val purchaseDao: PurchaseDao
) : PurchaseRepository {
    override suspend fun insertPurchase(purchase: Purchase, purchaseItems: List<PurchaseItem>) {
        val purchaseId = purchaseDao.insertPurchase(purchase)
        purchaseItems.forEach { 
            purchaseDao.insertPurchaseItem(it.copy(purchaseId = purchaseId))
        }
    }

    override fun getPurchasesForSupplier(supplierId: String): Flow<List<Purchase>> = purchaseDao.getPurchasesForSupplier(supplierId)
}
