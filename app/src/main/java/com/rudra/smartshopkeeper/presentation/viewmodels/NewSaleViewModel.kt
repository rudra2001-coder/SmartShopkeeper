
package com.rudra.smartshopkeeper.presentation.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.data.database.entities.CartItem
import com.rudra.smartshopkeeper.data.database.entities.Customer
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.data.database.entities.Sale
import com.rudra.smartshopkeeper.data.database.entities.SaleItem
import com.rudra.smartshopkeeper.domain.repositories.CustomerRepository
import com.rudra.smartshopkeeper.domain.repositories.ProductRepository
import com.rudra.smartshopkeeper.domain.repositories.SaleRepository
import com.rudra.smartshopkeeper.util.PdfGenerator
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

    init {
        viewModelScope.launch {
            customerRepository.getAllCustomers().collect { customers ->
                _state.update { it.copy(customers = customers) }
            }
        }
    }

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
        val cartItems = _state.value.cartItems.toMutableList()
        val existingCartItem = cartItems.find { it.product.id == product.id }
        if (existingCartItem != null) {
            val updatedCartItem = existingCartItem.copy(quantity = existingCartItem.quantity + 1)
            cartItems[cartItems.indexOf(existingCartItem)] = updatedCartItem
        } else {
            cartItems.add(CartItem(product, 1.0))
        }
        _state.update { it.copy(cartItems = cartItems) }
    }

    fun onRemoveFromCart(cartItem: CartItem) {
        val cartItems = _state.value.cartItems.toMutableList()
        cartItems.remove(cartItem)
        _state.update { it.copy(cartItems = cartItems) }
    }

    fun onQuantityChange(cartItem: CartItem, quantity: Double) {
        val cartItems = _state.value.cartItems.toMutableList()
        val index = cartItems.indexOf(cartItem)
        if (index != -1) {
            if (quantity > 0) {
                cartItems[index] = cartItem.copy(quantity = quantity)
            } else {
                cartItems.removeAt(index)
            }
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

            val saleId = saleRepository.insertSale(sale)
            val saleItems = mutableListOf<SaleItem>()

            state.cartItems.forEach { cartItem ->
                val saleItem = SaleItem(
                    saleId = saleId,
                    productId = cartItem.product.id,
                    productName = cartItem.product.name,
                    quantity = cartItem.quantity,
                    unitPrice = cartItem.product.salePrice,
                    totalPrice = cartItem.product.salePrice * cartItem.quantity
                )
                saleRepository.insertSaleItem(saleItem)
                productRepository.decreaseStock(cartItem.product.id, cartItem.quantity)
                saleItems.add(saleItem)
            }

            if (sale.dueAmount > 0 && sale.customerId != null) {
                customerRepository.increaseDue(sale.customerId, sale.dueAmount)
            }

            Toast.makeText(context, "বিক্রয় সফল!", Toast.LENGTH_SHORT).show()

            _state.update { it.copy(isSaleCompleted = true, completedSale = sale, completedSaleItems = saleItems) } 
        }
    }

    fun printInvoice(context: Context) {
        val state = _state.value
        if (state.isSaleCompleted) {
            PdfGenerator.generateInvoice(context, state.completedSale!!, state.completedSaleItems)
        }
    }
}

data class NewSaleState(
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList(),
    val customers: List<Customer> = emptyList(),
    val selectedCustomer: Customer? = null,
    val cartItems: List<CartItem> = emptyList(),
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val isSaleCompleted: Boolean = false,
    val completedSale: Sale? = null,
    val completedSaleItems: List<SaleItem> = emptyList()
) {
    val subtotal: Double
        get() = cartItems.sumOf { it.product.salePrice * it.quantity }

    val cartTotal: Double
        get() = subtotal - discount + tax
}
