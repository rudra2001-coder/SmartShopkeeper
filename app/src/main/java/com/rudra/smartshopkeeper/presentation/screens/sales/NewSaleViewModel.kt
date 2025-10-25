
package com.rudra.smartshopkeeper.presentation.screens.sales

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.data.database.entities.Customer
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.data.database.entities.Sale
import com.rudra.smartshopkeeper.data.database.entities.SaleItem
import com.rudra.smartshopkeeper.domain.repositories.CustomerRepository
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
class NewSaleViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewSaleState())
    val state: StateFlow<NewSaleState> = _state.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            productRepository.searchProducts(query).collect { products ->
                _state.update { it.copy(searchResults = products) }
            }
        }
    }

    fun onCustomerSelected(customer: Customer) {
        _state.update { it.copy(selectedCustomer = customer) }
    }

    fun onAddToCart(product: Product) {
        val cartItems = _state.value.cartItems.toMutableMap()
        val quantity = cartItems[product] ?: 0.0
        cartItems[product] = quantity + 1
        _state.update { it.copy(cartItems = cartItems) }
    }

    fun onRemoveFromCart(product: Product) {
        val cartItems = _state.value.cartItems.toMutableMap()
        cartItems.remove(product)
        _state.update { it.copy(cartItems = cartItems) }
    }

    fun onQuantityChange(product: Product, quantity: Double) {
        val cartItems = _state.value.cartItems.toMutableMap()
        if (quantity > 0) {
            cartItems[product] = quantity
        } else {
            cartItems.remove(product)
        }
        _state.update { it.copy(cartItems = cartItems) }
    }

    fun completeSale(context: Context) {
        viewModelScope.launch {
            val state = _state.value
            if (state.cartItems.isEmpty()) {
                Toast.makeText(context, "Cart is empty!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val sale = Sale(
                invoiceNumber = "INV-${System.currentTimeMillis()}", // Simple invoice number
                customerId = state.selectedCustomer?.id,
                totalAmount = state.cartTotal,
                paidAmount = state.cartTotal, // Assuming full payment for now
                dueAmount = 0.0, // Assuming full payment for now
                discount = state.discount,
                tax = state.tax
            )

            saleRepository.insertSale(sale)

            state.cartItems.forEach { (product, quantity) ->
                val saleItem = SaleItem(
                    saleId = sale.id,
                    productId = product.id,
                    productName = product.name,
                    quantity = quantity,
                    unitPrice = product.salePrice,
                    totalPrice = product.salePrice * quantity
                )
                saleRepository.insertSaleItem(saleItem)
                productRepository.decreaseStock(product.id, quantity)
            }

            if (sale.dueAmount > 0 && sale.customerId != null) {
                customerRepository.increaseDue(sale.customerId, sale.dueAmount)
            }

            Toast.makeText(context, "বিক্রয় সফল!", Toast.LENGTH_SHORT).show()

            // Reset state after sale
            _state.update { NewSaleState() }
        }
    }
}

data class NewSaleState(
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList(),
    val selectedCustomer: Customer? = null,
    val cartItems: Map<Product, Double> = emptyMap(),
    val discount: Double = 0.0,
    val tax: Double = 0.0
) {
    val subtotal: Double
        get() = cartItems.entries.sumOf { (product, quantity) ->
            product.salePrice * quantity
        }

    val cartTotal: Double
        get() = subtotal - discount + tax
}
