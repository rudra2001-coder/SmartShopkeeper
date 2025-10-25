
package com.rudra.smartshopkeeper.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rudra.smartshopkeeper.data.database.entities.Customer
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name")
    fun getAllCustomers(): Flow<List<Customer>>
    
    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: String): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)
    
    @Update
    suspend fun updateCustomer(customer: Customer)
    
    @Query("UPDATE customers SET totalDue = totalDue + :amount WHERE id = :customerId")
    suspend fun increaseDue(customerId: String, amount: Double)
    
    @Query("UPDATE customers SET totalDue = totalDue - :amount, totalPurchase = totalPurchase + :amount WHERE id = :customerId")
    suspend fun receivePayment(customerId: String, amount: Double)
    
    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Query("SELECT SUM(totalDue) FROM customers")
    suspend fun getTotalDue(): Double?
}
