
package com.rudra.smartshopkeeper.domain.repositories

import com.rudra.smartshopkeeper.data.database.entities.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun insertExpense(expense: Expense)
    fun getAllExpenses(): Flow<List<Expense>>
    suspend fun getTotalExpensesBetweenDates(startDate: Long, endDate: Long): Double?
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<Expense>>
    suspend fun getExpenseById(id: Long): Expense?
    suspend fun updateExpense(expense: Expense)
}
