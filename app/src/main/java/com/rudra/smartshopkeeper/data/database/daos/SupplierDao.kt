
package com.rudra.smartshopkeeper.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rudra.smartshopkeeper.data.database.entities.Supplier
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers ORDER BY name")
    fun getAllSuppliers(): Flow<List<Supplier>>

    @Query("SELECT * FROM suppliers WHERE id = :id")
    suspend fun getSupplierById(id: String): Supplier?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier)

    @Update
    suspend fun updateSupplier(supplier: Supplier)

    @Query("UPDATE suppliers SET totalDue = totalDue + :amount WHERE id = :supplierId")
    suspend fun increaseDue(supplierId: String, amount: Double)

    @Query("UPDATE suppliers SET totalDue = totalDue - :amount WHERE id = :supplierId")
    suspend fun decreaseDue(supplierId: String, amount: Double)
}
