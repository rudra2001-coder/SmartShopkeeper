
package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.data.database.entities.Supplier
import com.rudra.smartshopkeeper.domain.repositories.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditSupplierViewModel @Inject constructor(
    private val supplierRepository: SupplierRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val supplierId: String? = savedStateHandle.get<String>("supplierId")

    private val _supplier = MutableStateFlow<Supplier?>(null)
    val supplier = _supplier.asStateFlow()

    init {
        if (supplierId != null) {
            viewModelScope.launch {
                _supplier.value = supplierRepository.getSupplierById(supplierId)
            }
        }
    }

    fun saveSupplier(name: String, phone: String, address: String) {
        viewModelScope.launch {
            val supplierToSave = supplier.value?.copy(
                name = name,
                phone = phone,
                address = address
            ) ?: Supplier(
                id = "S_${System.currentTimeMillis()}",
                name = name,
                phone = phone,
                address = address
            )

            if (supplierId == null) {
                supplierRepository.insertSupplier(supplierToSave)
            } else {
                supplierRepository.updateSupplier(supplierToSave)
            }
        }
    }
}
