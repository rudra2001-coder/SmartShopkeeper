
package com.rudra.smartshopkeeper.presentation.screens.purchase

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.data.database.entities.Supplier
import com.rudra.smartshopkeeper.presentation.components.BengaliSearchBar
import com.rudra.smartshopkeeper.presentation.components.BengaliText
import com.rudra.smartshopkeeper.presentation.viewmodels.AddPurchaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseScreen(
    viewModel: AddPurchaseViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { BengaliText(text = "নতুন ক্রয়", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
            PurchaseBottomBar(
                totalAmount = state.purchaseTotal,
                onCompletePurchase = {
                    viewModel.completePurchase()
                    onBack()
                },
                isValid = state.purchaseItems.isNotEmpty()
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            SupplierSelectionSection(suppliers = state.suppliers, selectedSupplier = state.selectedSupplier, onSupplierSelect = viewModel::onSupplierSelected)
            ProductSearchSection(state.searchQuery, viewModel::onSearchQueryChange, state.searchResults, viewModel::onProductSelected)
            PurchaseItemsSection(purchaseItems = state.purchaseItems, onQuantityChange = viewModel::onQuantityChange, onCostPriceChange = viewModel::onCostPriceChange)
        }
    }
}

@Composable
fun SupplierSelectionSection(suppliers: List<Supplier>, selectedSupplier: Supplier?, onSupplierSelect: (Supplier) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.padding(16.dp)) {
        BengaliText(text = "সরবরাহকারী নির্বাচন করুন", modifier = Modifier.padding(bottom = 8.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }, verticalAlignment = Alignment.CenterVertically) {
            Text(text = selectedSupplier?.name ?: "সরবরাহকারী নির্বাচন")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            suppliers.forEach { supplier ->
                DropdownMenuItem(text = { Text(supplier.name) }, onClick = { 
                    onSupplierSelect(supplier)
                    expanded = false 
                })
            }
        }
    }
}

@Composable
fun ProductSearchSection(searchQuery: String, onSearchQueryChange: (String) -> Unit, searchResults: List<Product>, onProductSelect: (Product) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        BengaliSearchBar(query = searchQuery, onQueryChange = onSearchQueryChange)
        LazyColumn {
            items(searchResults) { product ->
                Text(text = product.name, modifier = Modifier.padding(8.dp).clickable { onProductSelect(product) })
            }
        }
    }
}

@Composable
fun PurchaseItemsSection(purchaseItems: Map<Product, Pair<Double, Double>>, onQuantityChange: (Product, Double) -> Unit, onCostPriceChange: (Product, Double) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(purchaseItems.keys.toList()) { product ->
            val (quantity, costPrice) = purchaseItems[product]!!
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = product.name, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BengaliText(text = "পরিমাণ:")
                        TextField(
                            value = quantity.toString(),
                            onValueChange = { onQuantityChange(product, it.toDoubleOrNull() ?: 1.0) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(100.dp)
                        )
                    }
                } 
                Column {
                    BengaliText(text = "ক্রয় মূল্য")
                    TextField(
                        value = costPrice.toString(),
                        onValueChange = { onCostPriceChange(product, it.toDoubleOrNull() ?: 0.0) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(120.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PurchaseBottomBar(
    totalAmount: Double,
    onCompletePurchase: () -> Unit,
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
                onClick = onCompletePurchase,
                enabled = isValid,
                modifier = Modifier.height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                BengaliText(
                    text = "ক্রয় সম্পন্ন",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
