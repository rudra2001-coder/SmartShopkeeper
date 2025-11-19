package com.rudra.smartshopkeeper.presentation.viewmodels

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.smartshopkeeper.data.database.entities.Product

data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.salePrice * quantity
}
@Composable
fun NewSaleScreen(
    onCartItemQuantityChanged: (String, Int) -> Unit,
    viewModel: NewSaleViewModel = hiltViewModel()
) {
    // ...
}
