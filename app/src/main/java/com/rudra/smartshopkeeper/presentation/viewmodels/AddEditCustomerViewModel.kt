
package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.data.database.entities.Customer
import com.rudra.smartshopkeeper.domain.repositories.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditCustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    var uiState by mutableStateOf(AddEditCustomerState())
        private set

    private var currentCustomerId: String? = null

    fun loadCustomer(customerId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val customer = customerRepository.getCustomerById(customerId)
                customer?.let {
                    uiState = uiState.copy(
                        name = it.name,
                        phone = it.phone ?: "",
                        address = it.address ?: "",
                        isLoading = false
                    )
                    currentCustomerId = customerId
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    errorMessage = "গ্রাহক লোড করতে সমস্যা: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun onNameChange(name: String) {
        uiState = uiState.copy(name = name, nameError = null)
    }

    fun onPhoneChange(phone: String) {
        uiState = uiState.copy(phone = phone)
    }

    fun onAddressChange(address: String) {
        uiState = uiState.copy(address = address)
    }

    fun validateForm(): Boolean {
        if (uiState.name.isBlank()) {
            uiState = uiState.copy(nameError = "গ্রাহকের নাম প্রয়োজন")
            return false
        }
        return true
    }

    fun saveCustomer() {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)
            try {
                val isNewCustomer = currentCustomerId == null
                val customer = Customer(
                    id = if (isNewCustomer) UUID.randomUUID().toString() else currentCustomerId!!,
                    name = uiState.name.trim(),
                    phone = uiState.phone.trim().takeIf { it.isNotBlank() },
                    address = uiState.address.trim().takeIf { it.isNotBlank() }
                )

                if (isNewCustomer) {
                    customerRepository.insertCustomer(customer)
                } else {
                    customerRepository.updateCustomer(customer)
                }

                uiState = uiState.copy(
                    isSaving = false,
                    saveSuccess = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    errorMessage = "গ্রাহক সেভ করতে সমস্যা: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

data class AddEditCustomerState(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    
    val nameError: String? = null,
    
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)
