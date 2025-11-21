
package com.rudra.smartshopkeeper.data.repositories

import com.rudra.smartshopkeeper.data.database.daos.ExpenseDao
import com.rudra.smartshopkeeper.data.database.entities.Expense
import com.rudra.smartshopkeeper.domain.repositories.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    override suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)
    override fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()
    override suspend fun getTotalExpensesBetweenDates(startDate: Long, endDate: Long): Double? = expenseDao.getTotalExpensesBetweenDates(startDate, endDate)
    override fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<Expense>> = expenseDao.getExpensesBetweenDates(startDate, endDate)
    override suspend fun getExpenseById(id: Long): Expense? = expenseDao.getExpenseById(id)
    override suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
}
