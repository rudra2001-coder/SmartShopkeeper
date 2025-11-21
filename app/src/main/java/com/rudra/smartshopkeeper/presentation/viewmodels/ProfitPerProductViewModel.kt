
package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.domain.repositories.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ProfitPerProductViewModel @Inject constructor(
    private val saleRepository: SaleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfitPerProductState())
    val state: StateFlow<ProfitPerProductState> = _state.asStateFlow()

    init {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        calendar.add(Calendar.MONTH, -1)
        val startDate = calendar.timeInMillis
        setStartDate(startDate)
        setEndDate(endDate)
        loadProfitPerProduct()
    }

    fun setStartDate(date: Long) {
        _state.update { it.copy(startDate = date) }
    }

    fun setEndDate(date: Long) {
        _state.update { it.copy(endDate = date) }
    }

    fun loadProfitPerProduct() {
        viewModelScope.launch {
            val startDate = _state.value.startDate
            val endDate = _state.value.endDate
            val profitPerProduct = saleRepository.getProfitPerProduct(startDate, endDate)
            _state.update { it.copy(profitPerProduct = profitPerProduct) }
        }
    }
}

data class ProfitPerProductState(
    val profitPerProduct: Map<String, Double> = emptyMap(),
    val startDate: Long = 0L,
    val endDate: Long = 0L
)
