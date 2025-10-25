
package com.rudra.smartshopkeeper.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.domain.repositories.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditProductUiState())
    val uiState: StateFlow<AddEditProductUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onBengaliNameChange(bengaliName: String) {
        _uiState.update { it.copy(bengaliName = bengaliName) }
    }

    fun onSalePriceChange(salePrice: String) {
        _uiState.update { it.copy(salePrice = salePrice) }
    }

    fun onCostPriceChange(costPrice: String) {
        _uiState.update { it.copy(costPrice = costPrice) }
    }

    fun onStockQtyChange(stockQty: String) {
        _uiState.update { it.copy(stockQty = stockQty) }
    }

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            val product = productRepository.getProductById(productId)
            product?.let {
                _uiState.update {
                    it.copy(
                        product = product,
                        name = product.name,
                        bengaliName = product.bengaliName ?: "",
                        salePrice = product.salePrice.toString(),
                        costPrice = product.costPrice.toString(),
                        stockQty = product.stockQty.toString()
                    )
                }
            }
        }
    }

    fun saveProduct() {
        viewModelScope.launch {
            val state = _uiState.value
            val product = state.product?.copy(
                name = state.name,
                bengaliName = state.bengaliName,
                salePrice = state.salePrice.toDoubleOrNull() ?: 0.0,
                costPrice = state.costPrice.toDoubleOrNull() ?: 0.0,
                stockQty = state.stockQty.toDoubleOrNull() ?: 0.0
            ) ?: Product(
                name = state.name,
                bengaliName = state.bengaliName,
                salePrice = state.salePrice.toDoubleOrNull() ?: 0.0,
                costPrice = state.costPrice.toDoubleOrNull() ?: 0.0,
                stockQty = state.stockQty.toDoubleOrNull() ?: 0.0
            )

            if (state.product == null) {
                productRepository.insertProduct(product)
            } else {
                productRepository.updateProduct(product)
            }
        }
    }
}

data class AddEditProductUiState(
    val product: Product? = null,
    val name: String = "",
    val bengaliName: String = "",
    val salePrice: String = "",
    val costPrice: String = "",
    val stockQty: String = ""
)
