package com.rudra.smartshopkeeper.presentation.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.rudra.smartshopkeeper.presentation.components.CustomerSelectionDialog
import com.rudra.smartshopkeeper.presentation.viewmodels.CartItem
import com.rudra.smartshopkeeper.presentation.viewmodels.NewSaleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSaleScreen(
    viewModel: NewSaleViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToAddCustomer: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showCustomerDialog by remember { mutableStateOf(false) }

    if (showCustomerDialog) {
        CustomerSelectionDialog(
            customers = state.customers,
            onCustomerSelected = {
                viewModel.onCustomerSelected(it)
                showCustomerDialog = false
            },
            onDismiss = { showCustomerDialog = false },
            onAddNewCustomer = onNavigateToAddCustomer
        )
    }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পিছনে")
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
            CustomerSelectionSection(
                selectedCustomer = state.selectedCustomer,
                onSelectCustomerClick = { showCustomerDialog = true }
            )

            ProductSelectionSection(
                searchQuery = state.searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                searchResults = state.searchResults,
                onProductSelect = { viewModel.onProductAddedToCart(it) }
            )

            CartItemsSection(
                cartItems = state.cartItems,
                onQuantityChange = { product, quantity -> 
                    viewModel.onCartItemQuantityChanged(product, quantity) 
                },
                onRemoveItem = { viewModel.onCartItemRemoved(it) }
            )
        }
    }
}

@Composable
fun CustomerSelectionSection(
    selectedCustomer: Customer?,
    onSelectCustomerClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (selectedCustomer != null) {
            BengaliText(text = "গ্রাহক: ${selectedCustomer.name}")
        } else {
            BengaliText(text = "গ্রাহক নির্বাচন করুন")
        }
        OutlinedButton(onClick = onSelectCustomerClick) {
            BengaliText(text = "নির্বাচন")
        }
    }
}

@Composable
fun ProductSelectionSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<Product>,
    onProductSelect: (Product) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        BengaliSearchBar(query = searchQuery, onQueryChange = onSearchQueryChange)
        LazyColumn {
            items(searchResults) { product ->
                ProductListItem(product = product, onProductSelect = onProductSelect)
            }
        }
    }
}

@Composable
fun ProductListItem(product: Product, onProductSelect: (Product) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            BengaliText(text = product.name, fontWeight = FontWeight.Bold)
            BengaliText(text = "৳${product.salePrice}", fontSize = 14.sp)
        }
        Button(onClick = { onProductSelect(product) }) {
            BengaliText(text = "যোগ করুন")
        }
    }
}

@Composable
fun CartItemsSection(
    cartItems: List<CartItem>,
    onQuantityChange: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(cartItems) { cartItem ->
            CartListItem(cartItem = cartItem, onQuantityChange = onQuantityChange, onRemoveItem = onRemoveItem)
        }
    }
}

@Composable
fun CartListItem(
    cartItem: CartItem,
    onQuantityChange: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            BengaliText(text = cartItem.product.name, fontWeight = FontWeight.Bold)
            BengaliText(text = "৳${cartItem.product.salePrice}", fontSize = 14.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onQuantityChange(cartItem, cartItem.quantity - 1) }) {
                Icon(Icons.Default.Remove, contentDescription = "Remove")
            }
            BengaliText(text = cartItem.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = { onQuantityChange(cartItem, cartItem.quantity + 1) }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
            IconButton(onClick = { onRemoveItem(cartItem) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove")
            }
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
                    text = "৳ ${"%.2f".format(totalAmount)}",
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

