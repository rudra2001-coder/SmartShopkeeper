package com.rudra.smartshopkeeper.presentation.viewmodels

import android.content.Context
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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewSaleViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository,
    private val saleRepository: SaleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewSaleState())
    val state: StateFlow<NewSaleState> = _state.asStateFlow()

    init {
        loadCustomers()
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            customerRepository.getAllCustomers().collect { customers ->
                _state.update { it.copy(customers = customers) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.isNotBlank()) {
            viewModelScope.launch {
                productRepository.searchProducts(query).collect { products ->
                    _state.update { it.copy(searchResults = products) }
                }
            }
        }
    }

    fun onCustomerSelected(customer: Customer) {
        _state.update { it.copy(selectedCustomer = customer) }
    }

    fun onProductAddedToCart(product: Product) {
        val cartItems = _state.value.cartItems.toMutableList()
        val existingCartItem = cartItems.find { it.product.id == product.id }

        if (existingCartItem != null) {
            val updatedCartItem = existingCartItem.copy(quantity = existingCartItem.quantity + 1)
            cartItems[cartItems.indexOf(existingCartItem)] = updatedCartItem
        } else {
            cartItems.add(CartItem(product = product))
        }

        _state.update { it.copy(cartItems = cartItems) }
        updateTotals()
    }

    fun onCartItemQuantityChanged(cartItem: CartItem, newQuantity: Int) {
        val cartItems = _state.value.cartItems.toMutableList()
        val existingCartItem = cartItems.find { it.product.id == cartItem.product.id }

        if (existingCartItem != null) {
            val updatedCartItem = existingCartItem.copy(quantity = newQuantity)
            cartItems[cartItems.indexOf(existingCartItem)] = updatedCartItem
            _state.update { it.copy(cartItems = cartItems) }
            updateTotals()
        }
    }

    fun onCartItemRemoved(cartItem: CartItem) {
        val cartItems = _state.value.cartItems.toMutableList()
        cartItems.remove(cartItem)
        _state.update { it.copy(cartItems = cartItems) }
        updateTotals()
    }

    private fun updateTotals() {
        val subtotal = _state.value.cartItems.sumOf { it.product.salePrice * it.quantity }
        _state.update { it.copy(subtotal = subtotal, cartTotal = subtotal) }
    }

    fun completeSale(context: Context) {
        viewModelScope.launch {
            val saleId = UUID.randomUUID().toString()
            val sale = Sale(
                id = saleId,
                invoiceNumber = UUID.randomUUID().toString(), // Or generate a more meaningful invoice number
                customerId = _state.value.selectedCustomer?.id,
                totalAmount = _state.value.cartTotal,
                paidAmount = _state.value.cartTotal, // Assuming full payment for now
                dueAmount = 0.0
            )

            saleRepository.insertSale(sale)

            _state.value.cartItems.forEach { cartItem ->
                val saleItem = SaleItem(
                    saleId = saleId,
                    productId = cartItem.product.id,
                    productName = cartItem.product.name,
                    quantity = cartItem.quantity.toDouble(),
                    unitPrice = cartItem.product.salePrice,
                    totalPrice = cartItem.product.salePrice * cartItem.quantity
                )
                saleRepository.insertSaleItem(saleItem)
                productRepository.decreaseStock(cartItem.product.id, cartItem.quantity.toDouble())
            }
        }
    }
}

data class NewSaleState(
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList(),
    val selectedCustomer: Customer? = null,
    val customers: List<Customer> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val cartTotal: Double = 0.0
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)
