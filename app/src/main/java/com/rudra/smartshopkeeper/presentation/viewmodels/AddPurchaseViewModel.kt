
package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.data.database.entities.Purchase
import com.rudra.smartshopkeeper.data.database.entities.PurchaseItem
import com.rudra.smartshopkeeper.data.database.entities.Supplier
import com.rudra.smartshopkeeper.domain.repositories.ProductRepository
import com.rudra.smartshopkeeper.domain.repositories.PurchaseRepository
import com.rudra.smartshopkeeper.domain.repositories.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPurchaseViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val supplierRepository: SupplierRepository,
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddPurchaseState())
    val state: StateFlow<AddPurchaseState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            supplierRepository.getAllSuppliers().collect { suppliers ->
                _state.update { it.copy(suppliers = suppliers) }
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

    fun onProductSelected(product: Product) {
        val purchaseItems = _state.value.purchaseItems.toMutableMap()
        purchaseItems[product] = 1.0 to product.costPrice
        _state.update { it.copy(purchaseItems = purchaseItems, searchQuery = "", searchResults = emptyList()) }
    }

    fun onSupplierSelected(supplier: Supplier) {
        _state.update { it.copy(selectedSupplier = supplier) }
    }

    fun onQuantityChange(product: Product, quantity: Double) {
        val purchaseItems = _state.value.purchaseItems.toMutableMap()
        if (quantity > 0) {
            val costPrice = purchaseItems[product]?.second ?: product.costPrice
            purchaseItems[product] = quantity to costPrice
        } else {
            purchaseItems.remove(product)
        }
        _state.update { it.copy(purchaseItems = purchaseItems) }
    }

    fun onCostPriceChange(product: Product, costPrice: Double) {
        val purchaseItems = _state.value.purchaseItems.toMutableMap()
        val quantity = purchaseItems[product]?.first ?: 1.0
        purchaseItems[product] = quantity to costPrice
        _state.update { it.copy(purchaseItems = purchaseItems) }
    }

    fun completePurchase() {
        viewModelScope.launch {
            val state = _state.value
            val purchase = Purchase(
                supplierId = state.selectedSupplier?.id,
                totalAmount = state.purchaseTotal,
                date = System.currentTimeMillis()
            )
            val purchaseItems = state.purchaseItems.map { (product, pair) ->
                val (quantity, costPrice) = pair
                PurchaseItem(
                    purchaseId = 0, // This will be replaced by the actual id
                    productId = product.id,
                    quantity = quantity,
                    costPrice = costPrice
                )
            }
            purchaseRepository.insertPurchase(purchase, purchaseItems)
            state.purchaseItems.forEach { (product, pair) ->
                val (quantity, _) = pair
                productRepository.increaseStock(product.id, quantity)
            }
            _state.update { AddPurchaseState() }
        }
    }
}

data class AddPurchaseState(
    val suppliers: List<Supplier> = emptyList(),
    val selectedSupplier: Supplier? = null,
    val purchaseItems: Map<Product, Pair<Double, Double>> = emptyMap(), // product to (quantity, costPrice)
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList()
) {
    val purchaseTotal: Double
        get() = purchaseItems.entries.sumOf { (_, pair) ->
            val (quantity, costPrice) = pair
            quantity * costPrice
        }
}
