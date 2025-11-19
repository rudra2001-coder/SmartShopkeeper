
package com.rudra.smartshopkeeper.domain.repositories

import com.rudra.smartshopkeeper.data.database.entities.Customer
import com.rudra.smartshopkeeper.data.database.entities.Transaction
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getAllCustomers(): Flow<List<Customer>>
    suspend fun getCustomerById(id: String): Customer?
    suspend fun insertCustomer(customer: Customer)
    suspend fun updateCustomer(customer: Customer)
    suspend fun increaseDue(customerId: String, amount: Double)
    suspend fun receivePayment(customerId: String, amount: Double)
    fun searchCustomers(query: String): Flow<List<Customer>>
    suspend fun getTotalDue(): Double
    fun getTransactionsForCustomer(customerId: String): Flow<List<Transaction>>
}
