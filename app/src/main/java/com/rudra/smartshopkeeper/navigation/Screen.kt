
package com.rudra.smartshopkeeper.navigation

sealed class Screen(val route: String) {
    object DashboardScreen : Screen("dashboard_screen")
    object NewSaleScreen : Screen("new_sale_screen")
    object ProductListScreen : Screen("product_list_screen")
    object AddEditProductScreen : Screen("add_edit_product_screen")
    object CustomerListScreen : Screen("customer_list_screen")
    object AddEditCustomerScreen : Screen("add_edit_customer_screen")
}
