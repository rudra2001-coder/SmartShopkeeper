
package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.data.database.entities.Sale
import com.rudra.smartshopkeeper.data.database.entities.SaleItem
import com.rudra.smartshopkeeper.domain.repositories.ProductRepository
import com.rudra.smartshopkeeper.domain.repositories.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaleReturnViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SaleReturnState())
    val state: StateFlow<SaleReturnState> = _state.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            saleRepository.searchSales(query).collect { sales ->
                _state.update { it.copy(searchResults = sales) }
            }
        }
    }

    fun onSaleSelected(sale: Sale) {
        viewModelScope.launch {
            val saleItems = saleRepository.getSaleItems(sale.id)
            _state.update { it.copy(selectedSale = sale, saleItems = saleItems, searchQuery = "", searchResults = emptyList()) }
        }
    }

    fun onItemSelected(saleItem: SaleItem, quantity: Double) {
        val returnedItems = _state.value.returnedItems.toMutableMap()
        if (quantity > 0) {
            returnedItems[saleItem] = quantity
        } else {
            returnedItems.remove(saleItem)
        }
        _state.update { it.copy(returnedItems = returnedItems) }
    }

    fun completeSaleReturn() {
        viewModelScope.launch {
            val state = _state.value
            val originalSale = state.selectedSale!!
            
            // Create a negative sale for the return
            val returnSale = Sale(
                invoiceNumber = "RET-${originalSale.invoiceNumber}",
                customerId = originalSale.customerId,
                totalAmount = -state.returnTotal,
                paidAmount = -state.returnTotal,
                dueAmount = 0.0,
                discount = 0.0,
                tax = 0.0
            )
            val returnSaleId = saleRepository.insertSale(returnSale)
            
            state.returnedItems.forEach { (saleItem, quantity) ->
                // Add stock back
                productRepository.increaseStock(saleItem.productId, quantity)
                
                // Create a negative sale item for the return
                val returnSaleItem = SaleItem(
                    saleId = returnSaleId,
                    productId = saleItem.productId,
                    productName = saleItem.productName,
                    quantity = -quantity,
                    unitPrice = saleItem.unitPrice,
                    totalPrice = -saleItem.unitPrice * quantity
                )
                saleRepository.insertSaleItem(returnSaleItem)
            }

            _state.update { SaleReturnState() } // Reset state
        }
    }
}

data class SaleReturnState(
    val selectedSale: Sale? = null,
    val saleItems: List<SaleItem> = emptyList(),
    val returnedItems: Map<SaleItem, Double> = emptyMap(),
    val searchQuery: String = "",
    val searchResults: List<Sale> = emptyList()
) {
    val returnTotal: Double
        get() = returnedItems.entries.sumOf { (saleItem, quantity) ->
            saleItem.unitPrice * quantity
        }
}
