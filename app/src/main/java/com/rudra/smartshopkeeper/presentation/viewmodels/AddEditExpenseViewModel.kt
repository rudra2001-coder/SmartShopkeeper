
package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.data.database.entities.Expense
import com.rudra.smartshopkeeper.domain.repositories.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val expenseId: Long? = savedStateHandle.get<Long>("expenseId")

    private val _expense = MutableStateFlow<Expense?>(null)
    val expense = _expense.asStateFlow()

    init {
        if (expenseId != null && expenseId != 0L) {
            viewModelScope.launch {
                _expense.value = expenseRepository.getExpenseById(expenseId)
            }
        }
    }

    suspend fun saveExpense(category: String, amount: Double, description: String) {
        val expenseToSave = expense.value?.copy(
            category = category,
            amount = amount,
            description = description
        ) ?: Expense(
            category = category,
            amount = amount,
            description = description,
            date = System.currentTimeMillis()
        )

        if (expenseId == null || expenseId == 0L) {
            expenseRepository.insertExpense(expenseToSave)
        } else {
            expenseRepository.updateExpense(expenseToSave)
        }
    }
}
