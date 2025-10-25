package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.data.database.entities.Customer
import com.rudra.smartshopkeeper.domain.repositories.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    fun addCustomer(name: String, phone: String, address: String) {
        viewModelScope.launch {
            val customer = Customer(
                name = name,
                phone = phone,
                address = address
            )
            customerRepository.insertCustomer(customer)
        }
    }
}