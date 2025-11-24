
package com.rudra.smartshopkeeper.presentation.screens.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.smartshopkeeper.data.database.entities.CartItem
import com.rudra.smartshopkeeper.data.database.entities.Customer
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.presentation.components.BengaliSearchBar
import com.rudra.smartshopkeeper.presentation.components.BengaliText
import com.rudra.smartshopkeeper.presentation.components.CustomerSelectionDialog
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
        containerColor = Color(0xFF1C2532),
        topBar = {
            TopAppBar(
                title = {
                    BengaliText(
                        text = "নতুন বিক্রয়",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পিছনে", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            SaleBottomBar(
                totalAmount = state.cartTotal,
                onCompleteSale = {
                    viewModel.completeSale(context)
                },
                isValid = state.cartItems.isNotEmpty(),
                isSaleCompleted = state.isSaleCompleted,
                onPrintInvoice = { viewModel.printInvoice(context) }
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
                onProductSelect = { viewModel.onAddToCart(it) }
            )

            CartItemsSection(
                cartItems = state.cartItems,
                onQuantityChange = { cartItem, quantity -> 
                    viewModel.onQuantityChange(cartItem, quantity) 
                },
                onRemoveItem = { viewModel.onRemoveFromCart(it) }
            )
        }
    }
}

@Composable
fun CustomerSelectionSection(
    selectedCustomer: Customer?,
    onSelectCustomerClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x402D3B50))
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
                    text = selectedCustomer?.name ?: "গ্রাহক নির্বাচন করুন", 
                    color = Color.White, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 18.sp
                )
                BengaliText(text = selectedCustomer?.phone ?: "", color = Color.Gray, fontSize = 14.sp)
            }
            Button(
                onClick = onSelectCustomerClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4AA))
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Select Customer", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                BengaliText(text = "নির্বাচন", color = Color.White)
            }
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
        LazyColumn(modifier = Modifier.height(200.dp)) {
            items(searchResults) { product ->
                ProductListItem(product = product, onProductSelect = onProductSelect)
            }
        }
    }
}

@Composable
fun ProductListItem(product: Product, onProductSelect: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onProductSelect(product) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x20FFFFFF))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                BengaliText(text = product.name, fontWeight = FontWeight.Bold, color = Color.White)
                BengaliText(text = "৳${product.salePrice}", fontSize = 14.sp, color = Color.Gray)
            }
            IconButton(
                onClick = { onProductSelect(product) },
                modifier = Modifier.background(Color(0xFF00D4AA), RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add to cart", tint = Color.White)
            }
        }
    }
}

@Composable
fun CartItemsSection(
    cartItems: List<CartItem>,
    onQuantityChange: (CartItem, Double) -> Unit,
    onRemoveItem: (CartItem) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(cartItems) { cartItem ->
            CartListItem(
                cartItem = cartItem, 
                onQuantityChange = { onQuantityChange(cartItem, it) }, 
                onRemoveItem = onRemoveItem
            )
        }
    }
}

@Composable
fun CartListItem(
    cartItem: CartItem,
    onQuantityChange: (Double) -> Unit,
    onRemoveItem: (CartItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x402D3B50))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                BengaliText(text = cartItem.product.name, fontWeight = FontWeight.Bold, color = Color.White)
                BengaliText(text = "৳${cartItem.product.salePrice}", fontSize = 14.sp, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onQuantityChange(cartItem.quantity - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Remove", tint = Color.White)
                }
                BengaliText(text = cartItem.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp), color = Color.White)
                IconButton(onClick = { onQuantityChange(cartItem.quantity + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                }
                IconButton(onClick = { onRemoveItem(cartItem) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                }
            }
        }
    }
}


@Composable
fun SaleBottomBar(
    totalAmount: Double,
    onCompleteSale: () -> Unit,
    isValid: Boolean,
    isSaleCompleted: Boolean,
    onPrintInvoice: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C2532).copy(alpha = 0.8f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                BengaliText(
                    text = "মোট টাকা",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                BengaliText(
                    text = "৳ ${"%.2f".format(totalAmount)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            if (isSaleCompleted) {
                Button(
                    onClick = onPrintInvoice,
                    modifier = Modifier.height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    BengaliText(
                        text = "ইনভয়েস প্রিন্ট",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = onCompleteSale,
                    enabled = isValid,
                    modifier = Modifier.height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4AA))
                ) {
                    BengaliText(
                        text = "বিক্রয় সম্পন্ন",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
