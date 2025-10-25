
package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.domain.repositories.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    var uiState by mutableStateOf(AddEditProductState())
        private set

    private var currentProductId: String? = null

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val product = productRepository.getProductById(productId)
                product?.let {
                    uiState = uiState.copy(
                        name = it.name,
                        bengaliName = it.bengaliName ?: "",
                        salePrice = it.salePrice.toString(),
                        costPrice = it.costPrice.toString(),
                        stockQty = it.stockQty.toString(),
                        unit = it.unit,
                        category = it.category,
                        minStockAlert = it.minStockAlert.toString(),
                        isLoading = false
                    )
                    currentProductId = productId
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    errorMessage = "পণ্য লোড করতে সমস্যা: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun onNameChange(name: String) {
        uiState = uiState.copy(name = name, nameError = null)
    }

    fun onBengaliNameChange(bengaliName: String) {
        uiState = uiState.copy(bengaliName = bengaliName)
    }

    fun onSalePriceChange(price: String) {
        uiState = uiState.copy(salePrice = price, salePriceError = null)
    }

    fun onCostPriceChange(price: String) {
        uiState = uiState.copy(costPrice = price, costPriceError = null)
    }

    fun onStockQtyChange(qty: String) {
        uiState = uiState.copy(stockQty = qty, stockQtyError = null)
    }

    fun onUnitChange(unit: String) {
        uiState = uiState.copy(unit = unit, unitError = null)
    }

    fun onCategoryChange(category: String) {
        uiState = uiState.copy(category = category)
    }

    fun onMinStockAlertChange(alert: String) {
        uiState = uiState.copy(minStockAlert = alert)
    }

    fun validateForm(): Boolean {
        val errors = mutableListOf<String>()

        if (uiState.name.isBlank()) {
            uiState = uiState.copy(nameError = "পণ্যের নাম প্রয়োজন")
            errors.add("name")
        }

        if (uiState.unit.isBlank()) {
            uiState = uiState.copy(unitError = "একক প্রয়োজন")
            errors.add("unit")
        }

        if (uiState.salePrice.isBlank()) {
            uiState = uiState.copy(salePriceError = "বিক্রয় মূল্য প্রয়োজন")
            errors.add("salePrice")
        } else {
            val price = uiState.salePrice.toDoubleOrNull()
            if (price == null || price <= 0) {
                uiState = uiState.copy(salePriceError = "বিক্রয় মূল্য বৈধ হতে হবে")
                errors.add("salePrice")
            }
        }

        if (uiState.costPrice.isNotBlank()) {
            val cost = uiState.costPrice.toDoubleOrNull()
            if (cost == null || cost < 0) {
                uiState = uiState.copy(costPriceError = "ক্রয় মূল্য বৈধ হতে হবে")
                errors.add("costPrice")
            }
        }

        if (uiState.stockQty.isBlank()) {
            uiState = uiState.copy(stockQtyError = "স্টক পরিমাণ প্রয়োজন")
            errors.add("stockQty")
        } else {
            val stock = uiState.stockQty.toDoubleOrNull()
            if (stock == null || stock < 0) {
                uiState = uiState.copy(stockQtyError = "স্টক পরিমাণ বৈধ হতে হবে")
                errors.add("stockQty")
            }
        }

        if (uiState.minStockAlert.isNotBlank()) {
            val minStock = uiState.minStockAlert.toDoubleOrNull()
            if (minStock == null || minStock < 0) {
                uiState = uiState.copy(minStockAlert = "0")
            }
        }

        return errors.isEmpty()
    }

    fun saveProduct() {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)
            try {
                val isNewProduct = currentProductId == null
                val product = Product(
                    id = if (isNewProduct) UUID.randomUUID().toString() else currentProductId!!,
                    name = uiState.name.trim(),
                    bengaliName = uiState.bengaliName.trim().takeIf { it.isNotBlank() },
                    salePrice = uiState.salePrice.toDouble(),
                    costPrice = uiState.costPrice.toDoubleOrNull() ?: 0.0,
                    stockQty = uiState.stockQty.toDouble(),
                    unit = uiState.unit.trim(),
                    category = uiState.category,
                    minStockAlert = uiState.minStockAlert.toDoubleOrNull() ?: 5.0
                )

                if (isNewProduct) {
                    productRepository.insertProduct(product)
                } else {
                    productRepository.updateProduct(product)
                }

                uiState = uiState.copy(
                    isSaving = false,
                    saveSuccess = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    errorMessage = "পণ্য সেভ করতে সমস্যা: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

data class AddEditProductState(
    val name: String = "",
    val bengaliName: String = "",
    val salePrice: String = "",
    val costPrice: String = "",
    val stockQty: String = "",
    val unit: String = "পিস",
    val category: String = "সাধারণ",
    val minStockAlert: String = "5",

    // Validation errors
    val nameError: String? = null,
    val salePriceError: String? = null,
    val costPriceError: String? = null,
    val stockQtyError: String? = null,
    val unitError: String? = null,

    // UI state
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,

    // Available categories
    val availableCategories: List<String> = listOf(
        "সাধারণ",
        "চাল-ডাল",
        "তেল-মসলা",
        "নিত্যপণ্য",
        "খাবার",
        "কৃষি",
        "স্টেশনারী",
        "ওষুধ",
        "ইলেকট্রনিক্স"
    )
)
