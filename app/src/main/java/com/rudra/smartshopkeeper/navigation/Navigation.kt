
package com.rudra.smartshopkeeper.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rudra.smartshopkeeper.presentation.screens.AddEditProductScreen
import com.rudra.smartshopkeeper.presentation.screens.customers.AddEditCustomerScreen
import com.rudra.smartshopkeeper.presentation.screens.customers.CustomerListScreen
import com.rudra.smartshopkeeper.presentation.screens.dashboard.DashboardScreen
import com.rudra.smartshopkeeper.presentation.screens.products.ProductListScreen
import com.rudra.smartshopkeeper.presentation.screens.sales.NewSaleScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.DashboardScreen.route) {
        composable(Screen.DashboardScreen.route) {
            DashboardScreen(
                onNavigateToSales = { navController.navigate(Screen.NewSaleScreen.route) },
                onNavigateToProducts = { navController.navigate(Screen.ProductListScreen.route) },
                onNavigateToCustomers = { navController.navigate(Screen.CustomerListScreen.route) },
                onNavigateToExpenses = { /*TODO*/ },
                onNavigateToReports = { /*TODO*/ }
            )
        }

        composable(Screen.NewSaleScreen.route) {
            NewSaleScreen(
                onBack = { navController.popBackStack() }
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
                    navController.navigate("${Screen.AddEditCustomerScreen.route}?customerId=$customerId")
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
    }
}
