
package com.rudra.smartshopkeeper.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.domain.repositories.CustomerRepository
import com.rudra.smartshopkeeper.domain.repositories.ExpenseRepository
import com.rudra.smartshopkeeper.domain.repositories.ProductRepository
import com.rudra.smartshopkeeper.domain.repositories.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                val todayStart = getStartOfDay()
                val todayEnd = getEndOfDay()

                val todaySales = saleRepository.getTotalSalesBetweenDates(todayStart, todayEnd) ?: 0.0
                val todayExpenses = expenseRepository.getTotalExpensesBetweenDates(todayStart, todayEnd) ?: 0.0
                val totalDue = customerRepository.getTotalDue()
                val lowStockProducts = productRepository.getLowStockProducts().first()
                val todayProfit = calculateTodayProfit(todayStart, todayEnd)

                _state.update {
                    it.copy(
                        todaySales = todaySales,
                        todayExpenses = todayExpenses,
                        totalDue = totalDue,
                        lowStockProducts = lowStockProducts,
                        todayProfit = todayProfit,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private suspend fun calculateTodayProfit(startDate: Long, endDate: Long): Double {
        val saleItems = saleRepository.getSaleItemsBetweenDates(startDate, endDate)
        return saleItems.sumOf { saleItem ->
            val product = productRepository.getProductById(saleItem.productId)
            val costPrice = product?.costPrice ?: 0.0
            (saleItem.unitPrice - costPrice) * saleItem.quantity
        }
    }

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}

data class DashboardState(
    val todaySales: Double = 0.0,
    val todayProfit: Double = 0.0,
    val todayExpenses: Double = 0.0,
    val totalDue: Double = 0.0,
    val lowStockProducts: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
