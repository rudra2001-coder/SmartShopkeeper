
package com.rudra.smartshopkeeper.data.repositories

import com.rudra.smartshopkeeper.data.database.daos.CustomerDao
import com.rudra.smartshopkeeper.data.database.daos.TransactionDao
import com.rudra.smartshopkeeper.data.database.entities.Customer
import com.rudra.smartshopkeeper.data.database.entities.Transaction
import com.rudra.smartshopkeeper.domain.repositories.CustomerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao
) : CustomerRepository {
    override fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()
    override suspend fun getCustomerById(id: String): Customer? = customerDao.getCustomerById(id)
    override suspend fun insertCustomer(customer: Customer) = customerDao.insertCustomer(customer)
    override suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)

    override suspend fun increaseDue(customerId: String, amount: Double) {
        val customer = customerDao.getCustomerById(customerId)
        if (customer != null) {
            val updatedCustomer = customer.copy(totalDue = customer.totalDue + amount)
            customerDao.updateCustomer(updatedCustomer)
            transactionDao.insertTransaction(
                Transaction(
                    customerId = customerId,
                    amount = amount,
                    type = "DUE",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun receivePayment(customerId: String, amount: Double) {
        val customer = customerDao.getCustomerById(customerId)
        if (customer != null) {
            val updatedCustomer = customer.copy(
                totalDue = customer.totalDue - amount,
                totalPaid = customer.totalPaid + amount
            )
            customerDao.updateCustomer(updatedCustomer)
            transactionDao.insertTransaction(
                Transaction(
                    customerId = customerId,
                    amount = amount,
                    type = "PAYMENT",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    override fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)
    override suspend fun getTotalDue(): Double = customerDao.getTotalDue() ?: 0.0
    override fun getTransactionsForCustomer(customerId: String): Flow<List<Transaction>> = transactionDao.getTransactionsForCustomer(customerId)
}
