
package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.domain.repositories.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ExpenseReportViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseReportState())
    val state: StateFlow<ExpenseReportState> = _state.asStateFlow()

    init {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        calendar.add(Calendar.MONTH, -1)
        val startDate = calendar.timeInMillis
        setStartDate(startDate)
        setEndDate(endDate)
        loadExpenseReport()
    }

    fun setStartDate(date: Long) {
        _state.update { it.copy(startDate = date) }
    }

    fun setEndDate(date: Long) {
        _state.update { it.copy(endDate = date) }
    }

    fun loadExpenseReport() {
        viewModelScope.launch {
            val startDate = _state.value.startDate
            val endDate = _state.value.endDate
            expenseRepository.getExpensesBetweenDates(startDate, endDate).collect { expenses ->
                val expensesByCategory = expenses.groupBy { it.category }
                _state.update { it.copy(expensesByCategory = expensesByCategory) }
            }
        }
    }
}

data class ExpenseReportState(
    val expensesByCategory: Map<String, List<com.rudra.smartshopkeeper.data.database.entities.Expense>> = emptyMap(),
    val startDate: Long = 0L,
    val endDate: Long = 0L
)
