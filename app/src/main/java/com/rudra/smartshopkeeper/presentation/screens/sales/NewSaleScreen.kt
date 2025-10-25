
package com.rudra.smartshopkeeper.presentation.screens.sales

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.smartshopkeeper.data.database.entities.Customer
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.presentation.components.BengaliSearchBar
import com.rudra.smartshopkeeper.presentation.components.BengaliText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSaleScreen(
    viewModel: NewSaleViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BengaliText(
                        text = "নতুন বিক্রয়",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "পিছনে")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            SaleBottomBar(
                totalAmount = state.cartTotal,
                onCompleteSale = {
                    viewModel.completeSale(context)
                    onBack()
                },
                isValid = state.cartItems.isNotEmpty()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            CustomerSelectionSection(selectedCustomer = state.selectedCustomer, onCustomerSelect = viewModel::onCustomerSelected, onAddNewCustomer = { /*TODO*/ })
            ProductSelectionSection(searchQuery = state.searchQuery, onSearchQueryChange = viewModel::onSearchQueryChange, searchResults = state.searchResults, onProductSelect = viewModel::onAddToCart)
            CartItemsSection(cartItems = state.cartItems, onQuantityChange = viewModel::onQuantityChange, onRemoveItem = viewModel::onRemoveFromCart)
            PaymentSummarySection(subtotal = state.subtotal, discount = state.discount, tax = state.tax, total = state.cartTotal)

        }
    }
}

@Composable
fun CustomerSelectionSection(selectedCustomer: Customer?, onCustomerSelect: (Customer) -> Unit, onAddNewCustomer: () -> Unit) {
    // TODO: Implement Customer Selection UI
}

@Composable
fun ProductSelectionSection(searchQuery: String, onSearchQueryChange: (String) -> Unit, searchResults: List<Product>, onProductSelect: (Product) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        BengaliSearchBar(query = searchQuery, onQueryChange = onSearchQueryChange)
        LazyColumn {
            items(searchResults) { product ->
                Text(text = product.name, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun CartItemsSection(cartItems: Map<Product, Double>, onQuantityChange: (Product, Double) -> Unit, onRemoveItem: (Product) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(cartItems.keys.toList()) { product ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = product.name)
                Text(text = "${cartItems[product]}")
            }
        }
    }
}

@Composable
fun PaymentSummarySection(subtotal: Double, discount: Double, tax: Double, total: Double) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Subtotal")
            Text(text = "$subtotal")
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Discount")
            Text(text = "$discount")
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Tax")
            Text(text = "$tax")
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Total")
            Text(text = "$total")
        }
    }
}

@Composable
fun SaleBottomBar(
    totalAmount: Double,
    onCompleteSale: () -> Unit,
    isValid: Boolean
) {
    Surface(
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                BengaliText(
                    text = "মোট টাকা",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BengaliText(
                    text = "৳ ${String.format("%.2f", totalAmount)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Button(
                onClick = onCompleteSale,
                enabled = isValid,
                modifier = Modifier.height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                BengaliText(
                    text = "বিক্রয় সম্পন্ন",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
