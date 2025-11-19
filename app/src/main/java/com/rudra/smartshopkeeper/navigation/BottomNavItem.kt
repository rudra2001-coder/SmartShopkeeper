
package com.rudra.smartshopkeeper.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Dashboard : BottomNavItem("Dashboard", Icons.Default.Dashboard, Screen.DashboardScreen.route)
    object NewSale : BottomNavItem("Sale", Icons.Default.PointOfSale, Screen.NewSaleScreen.route)
    object Products : BottomNavItem("Products", Icons.Default.Inventory, Screen.ProductListScreen.route)
    object Customers : BottomNavItem("Customers", Icons.Default.People, Screen.CustomerListScreen.route)
    object Reports : BottomNavItem("Reports", Icons.Default.Assessment, Screen.ReportScreen.route)
}
