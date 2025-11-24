
package com.rudra.smartshopkeeper.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rudra.smartshopkeeper.presentation.screens.AddEditProductScreen
import com.rudra.smartshopkeeper.presentation.screens.customers.AddEditCustomerScreen
import com.rudra.smartshopkeeper.presentation.screens.customers.CustomerLedgerScreen
import com.rudra.smartshopkeeper.presentation.screens.customers.CustomerListScreen
import com.rudra.smartshopkeeper.presentation.screens.customers.CustomerPurchaseHistoryScreen
import com.rudra.smartshopkeeper.presentation.screens.DashboardScreen
import com.rudra.smartshopkeeper.presentation.screens.expense.AddEditExpenseScreen
import com.rudra.smartshopkeeper.presentation.screens.expense.ExpenseListScreen
import com.rudra.smartshopkeeper.presentation.screens.expense.ExpenseReportScreen
import com.rudra.smartshopkeeper.presentation.screens.expense.ExpenseVsIncomeScreen
import com.rudra.smartshopkeeper.presentation.screens.products.ProductListScreen
import com.rudra.smartshopkeeper.presentation.screens.profit.ProfitPerCategoryScreen
import com.rudra.smartshopkeeper.presentation.screens.profit.ProfitPerProductScreen
import com.rudra.smartshopkeeper.presentation.screens.purchase.AddPurchaseScreen
import com.rudra.smartshopkeeper.presentation.screens.report.ReportScreen
import com.rudra.smartshopkeeper.presentation.screens.sales.NewSaleScreen
import com.rudra.smartshopkeeper.presentation.screens.sales.SaleReturnScreen
import com.rudra.smartshopkeeper.presentation.screens.supplier.AddEditSupplierScreen
import com.rudra.smartshopkeeper.presentation.screens.supplier.SupplierLedgerScreen
import com.rudra.smartshopkeeper.presentation.screens.supplier.SupplierListScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.DashboardScreen.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.DashboardScreen.route) {
                DashboardScreen(
                    onNavigateToSales = { navController.navigate(Screen.NewSaleScreen.route) },
                    onNavigateToProducts = { navController.navigate(Screen.ProductListScreen.route) },
                    onNavigateToCustomers = { navController.navigate(Screen.CustomerListScreen.route) },
                    onNavigateToExpenses = { navController.navigate(Screen.ExpenseListScreen.route) },
                    onNavigateToReports = { navController.navigate(Screen.ReportScreen.route) }
                )
            }

            composable(Screen.NewSaleScreen.route) {
                NewSaleScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToAddCustomer = { navController.navigate("${Screen.AddEditCustomerScreen.route}?customerId=null") }
                )
            }

            composable(Screen.ProductListScreen.route) {
                ProductListScreen(
                    onBack = { navController.popBackStack() },
                    onAddProduct = { navController.navigate("${Screen.AddEditProductScreen.route}?productId=null") },
                    onProductClick = { productId ->
                        navController.navigate("${Screen.AddEditProductScreen.route}?productId=$productId")
                    }
                )
            }

            composable(
                route = "${Screen.AddEditProductScreen.route}?productId={productId}",
                arguments = listOf(navArgument("productId") {
                    type = NavType.StringType
                    nullable = true
                })
            ) { backStackEntry ->
                AddEditProductScreen(
                    onBack = { navController.popBackStack() },
                    productId = backStackEntry.arguments?.getString("productId")
                )
            }

            composable(Screen.CustomerListScreen.route) {
                CustomerListScreen(
                    onBack = { navController.popBackStack() },
                    onAddCustomer = { navController.navigate("${Screen.AddEditCustomerScreen.route}?customerId=null") },
                    onCustomerClick = { customerId ->
                        navController.navigate("${Screen.CustomerLedgerScreen.route}/$customerId")
                    }
                )
            }

            composable(
                route = "${Screen.AddEditCustomerScreen.route}?customerId={customerId}",
                arguments = listOf(navArgument("customerId") {
                    type = NavType.StringType
                    nullable = true
                })
            ) { backStackEntry ->
                AddEditCustomerScreen(
                    onBack = { navController.popBackStack() },
                    customerId = backStackEntry.arguments?.getString("customerId")
                )
            }

            composable(
                route = "${Screen.CustomerLedgerScreen.route}/{customerId}",
                arguments = listOf(navArgument("customerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getString("customerId")
                CustomerLedgerScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToPurchaseHistory = { navController.navigate("${Screen.CustomerPurchaseHistoryScreen.route}/$customerId") }
                )
            }

            composable(
                route = "${Screen.CustomerPurchaseHistoryScreen.route}/{customerId}",
                arguments = listOf(navArgument("customerId") { type = NavType.StringType })
            ) { 
                CustomerPurchaseHistoryScreen(onBack = { navController.popBackStack() }, onSaleClick = { /* TODO */ })
            }

            composable(Screen.SupplierListScreen.route) {
                SupplierListScreen(
                    onBack = { navController.popBackStack() },
                    onAddSupplier = { navController.navigate("${Screen.AddEditSupplierScreen.route}?supplierId=null") },
                    onSupplierClick = { supplierId ->
                        navController.navigate("${Screen.SupplierLedgerScreen.route}/$supplierId")
                    }
                )
            }

            composable(
                route = "${Screen.AddEditSupplierScreen.route}?supplierId={supplierId}",
                arguments = listOf(navArgument("supplierId") {
                    type = NavType.StringType
                    nullable = true
                })
            ) { backStackEntry ->
                AddEditSupplierScreen(
                    onBack = { navController.popBackStack() },
                    supplierId = backStackEntry.arguments?.getString("supplierId")
                )
            }

            composable(
                route = "${Screen.SupplierLedgerScreen.route}/{supplierId}",
                arguments = listOf(navArgument("supplierId") { type = NavType.StringType })
            ) { 
                SupplierLedgerScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.ExpenseListScreen.route) {
                ExpenseListScreen(
                    onBack = { navController.popBackStack() },
                    onAddExpense = { navController.navigate("${Screen.AddEditExpenseScreen.route}?expenseId=0L") },
                    onExpenseClick = { expenseId ->
                        navController.navigate("${Screen.AddEditExpenseScreen.route}?expenseId=$expenseId")
                    },
                    onNavigateToExpenseReport = { navController.navigate(Screen.ExpenseReportScreen.route) }
                )
            }

            composable(
                route = "${Screen.AddEditExpenseScreen.route}?expenseId={expenseId}",
                arguments = listOf(navArgument("expenseId") {
                    type = NavType.LongType
                    defaultValue = 0L
                })
            ) { backStackEntry ->
                AddEditExpenseScreen(
                    onBack = { navController.popBackStack() },
                    expenseId = backStackEntry.arguments?.getLong("expenseId")
                )
            }

            composable(Screen.AddPurchaseScreen.route) {
                AddPurchaseScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.ReportScreen.route) {
                ReportScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToProfitPerProduct = { navController.navigate(Screen.ProfitPerProductScreen.route) },
                    onNavigateToProfitPerCategory = { navController.navigate(Screen.ProfitPerCategoryScreen.route) }
                )
            }
            
            composable(Screen.SaleReturnScreen.route) {
                SaleReturnScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.ExpenseReportScreen.route) {
                ExpenseReportScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToExpenseVsIncome = { navController.navigate(Screen.ExpenseVsIncomeScreen.route) }
                )
            }

            composable(Screen.ExpenseVsIncomeScreen.route) {
                ExpenseVsIncomeScreen(onBack = { navController.popBackStack() })
            }
            
            composable(Screen.ProfitPerProductScreen.route) {
                ProfitPerProductScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.ProfitPerCategoryScreen.route) {
                ProfitPerCategoryScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
