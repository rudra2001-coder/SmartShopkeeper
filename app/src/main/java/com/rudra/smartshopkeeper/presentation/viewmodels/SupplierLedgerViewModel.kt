
package com.rudra.smartshopkeeper.presentation.viewmodels

import android.content.Context
import android.os.Environment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.rudra.smartshopkeeper.data.database.entities.Purchase
import com.rudra.smartshopkeeper.data.database.entities.Supplier
import com.rudra.smartshopkeeper.domain.repositories.PurchaseRepository
import com.rudra.smartshopkeeper.domain.repositories.SupplierRepository
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
class SupplierLedgerViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val supplierRepository: SupplierRepository,
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    val supplierId: String = savedStateHandle.get<String>("supplierId")!!

    private val _supplier = MutableStateFlow<Supplier?>(null)
    val supplier = _supplier.asStateFlow()

    val purchaseHistory = purchaseRepository.getPurchasesForSupplier(supplierId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _supplier.value = supplierRepository.getSupplierById(supplierId)
        }
    }

    fun addDue(amount: Double) {
        viewModelScope.launch {
            supplierRepository.increaseDue(supplierId, amount)
            _supplier.value = supplierRepository.getSupplierById(supplierId) // Refresh supplier data
        }
    }

    fun makePayment(amount: Double) {
        viewModelScope.launch {
            supplierRepository.decreaseDue(supplierId, amount)
            _supplier.value = supplierRepository.getSupplierById(supplierId) // Refresh supplier data
        }
    }

    fun exportLedger(context: Context) {
        viewModelScope.launch {
            val supplierValue = supplier.first()
            val purchaseHistoryValue = purchaseHistory.first()

            if (supplierValue != null) {
                val fileName = "${supplierValue.name}_ledger.csv"
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)

                csvWriter().open(file) { 
                    writeRow("Date", "Total Amount")
                    purchaseHistoryValue.forEach { purchase ->
                        writeRow(purchase.date, purchase.totalAmount)
                    }
                }
                // TODO: Show a toast or notification to indicate success
            }
        }
    }
}
