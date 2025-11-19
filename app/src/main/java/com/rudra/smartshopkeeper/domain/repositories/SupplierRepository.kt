
package com.rudra.smartshopkeeper.domain.repositories

import com.rudra.smartshopkeeper.data.database.entities.Supplier
import kotlinx.coroutines.flow.Flow

interface SupplierRepository {
    fun getAllSuppliers(): Flow<List<Supplier>>
    suspend fun getSupplierById(id: String): Supplier?
    suspend fun insertSupplier(supplier: Supplier)
    suspend fun updateSupplier(supplier: Supplier)
    suspend fun increaseDue(supplierId: String, amount: Double)
    suspend fun decreaseDue(supplierId: String, amount: Double)
}
