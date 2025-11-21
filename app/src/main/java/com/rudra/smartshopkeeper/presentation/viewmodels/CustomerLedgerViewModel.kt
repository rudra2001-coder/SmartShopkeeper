
package com.rudra.smartshopkeeper.presentation.viewmodels

import android.content.Context
import android.os.Environment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.rudra.smartshopkeeper.data.database.entities.Customer
import com.rudra.smartshopkeeper.data.database.entities.Transaction
import com.rudra.smartshopkeeper.domain.repositories.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CustomerLedgerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    val customerId: String = savedStateHandle.get<String>("customerId")!!

    private val _customer = MutableStateFlow<Customer?>(null)
    val customer = _customer.asStateFlow()

    val transactions = customerRepository.getTransactionsForCustomer(customerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _customer.value = customerRepository.getCustomerById(customerId)
        }
    }

    fun addDue(amount: Double) {
        viewModelScope.launch {
            customerRepository.increaseDue(customerId, amount)
            _customer.value = customerRepository.getCustomerById(customerId) // Refresh customer data
        }
    }

    fun receivePayment(amount: Double) {
        viewModelScope.launch {
            customerRepository.receivePayment(customerId, amount)
            _customer.value = customerRepository.getCustomerById(customerId) // Refresh customer data
        }
    }

    fun exportLedger(context: Context) {
        viewModelScope.launch {
            val customerValue = customer.first()
            val transactionsValue = transactions.first()

            if (customerValue != null) {
                val fileName = "${customerValue.name}_ledger.csv"
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)

                csvWriter().open(file) { 
                    writeRow("Date", "Type", "Amount")
                    transactionsValue.forEach { transaction ->
                        writeRow(transaction.timestamp, transaction.type, transaction.amount)
                    }
                }
                // TODO: Show a toast or notification to indicate success
            }
        }
    }
}
