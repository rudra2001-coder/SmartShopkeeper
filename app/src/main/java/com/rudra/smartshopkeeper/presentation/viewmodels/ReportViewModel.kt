
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
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    init {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        calendar.add(Calendar.MONTH, -1)
        val startDate = calendar.timeInMillis
        setStartDate(startDate)
        setEndDate(endDate)
        loadReport()
    }

    fun setStartDate(date: Long) {
        _state.update { it.copy(startDate = date) }
    }

    fun setEndDate(date: Long) {
        _state.update { it.copy(endDate = date) }
    }

    fun loadReport() {
        viewModelScope.launch {
            val startDate = _state.value.startDate
            val endDate = _state.value.endDate
            val totalSales = saleRepository.getTotalSalesBetweenDates(startDate, endDate) ?: 0.0
            val totalCostOfGoodsSold = saleRepository.getTotalCostOfGoodsSold(startDate, endDate) ?: 0.0
            val totalExpenses = expenseRepository.getTotalExpensesBetweenDates(startDate, endDate) ?: 0.0
            val totalProfit = totalSales - totalCostOfGoodsSold - totalExpenses

            _state.update {
                it.copy(
                    totalSales = totalSales,
                    totalProfit = totalProfit,
                    totalExpenses = totalExpenses
                )
            }
        }
    }
}


data class ReportState(
    val totalSales: Double = 0.0,
    val totalProfit: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val startDate: Long = 0L,
    val endDate: Long = 0L
)
