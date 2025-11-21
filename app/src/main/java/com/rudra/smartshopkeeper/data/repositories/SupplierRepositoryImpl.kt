
package com.rudra.smartshopkeeper.data.repositories

import com.rudra.smartshopkeeper.data.database.daos.SupplierDao
import com.rudra.smartshopkeeper.data.database.entities.Supplier
import com.rudra.smartshopkeeper.domain.repositories.SupplierRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SupplierRepositoryImpl @Inject constructor(
    private val supplierDao: SupplierDao
) : SupplierRepository {
    override fun getAllSuppliers(): Flow<List<Supplier>> = supplierDao.getAllSuppliers()
    override suspend fun getSupplierById(id: String): Supplier? = supplierDao.getSupplierById(id)
    override suspend fun insertSupplier(supplier: Supplier) = supplierDao.insertSupplier(supplier)
    override suspend fun updateSupplier(supplier: Supplier) = supplierDao.updateSupplier(supplier)
    override suspend fun increaseDue(supplierId: String, amount: Double) = supplierDao.increaseDue(supplierId, amount)
    override suspend fun decreaseDue(supplierId: String, amount: Double) = supplierDao.decreaseDue(supplierId, amount)
}
