
package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.domain.repositories.ExpenseRepository
import com.rudra.smartshopkeeper.domain.repositories.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseVsIncomeViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseVsIncomeState())
    val state: StateFlow<ExpenseVsIncomeState> = _state.asStateFlow()

    fun loadChartData(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            val totalIncome = saleRepository.getTotalSalesBetweenDates(startDate, endDate) ?: 0.0
            val totalExpense = expenseRepository.getTotalExpensesBetweenDates(startDate, endDate) ?: 0.0
            _state.update { it.copy(totalIncome = totalIncome, totalExpense = totalExpense) }
        }
    }
}

data class ExpenseVsIncomeState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0
)
