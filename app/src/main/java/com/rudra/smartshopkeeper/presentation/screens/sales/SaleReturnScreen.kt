
package com.rudra.smartshopkeeper.presentation.screens.sales

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.smartshopkeeper.data.database.entities.Sale
import com.rudra.smartshopkeeper.data.database.entities.SaleItem
import com.rudra.smartshopkeeper.presentation.components.BengaliSearchBar
import com.rudra.smartshopkeeper.presentation.components.BengaliText
import com.rudra.smartshopkeeper.presentation.viewmodels.SaleReturnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleReturnScreen(
    viewModel: SaleReturnViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { BengaliText(text = "পণ্য ফেরত", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
            Button(
                onClick = {
                    viewModel.completeSaleReturn()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                enabled = state.returnedItems.isNotEmpty()
            ) {
                BengaliText("ফেরত সম্পন্ন করুন")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            SaleSearchSection(state.searchQuery, viewModel::onSearchQueryChange, state.searchResults, viewModel::onSaleSelected)
            if (state.selectedSale != null) {
                SaleReturnItemsSection(saleItems = state.saleItems, returnedItems = state.returnedItems, onItemSelected = viewModel::onItemSelected)
            }
        }
    }
}

@Composable
fun SaleSearchSection(searchQuery: String, onSearchQueryChange: (String) -> Unit, searchResults: List<Sale>, onSaleSelect: (Sale) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        BengaliSearchBar(query = searchQuery, onQueryChange = onSearchQueryChange)
        LazyColumn {
            items(searchResults) { sale ->
                Text(text = sale.invoiceNumber, modifier = Modifier.padding(8.dp).clickable { onSaleSelect(sale) })
            }
        }
    }
}

@Composable
fun SaleReturnItemsSection(saleItems: List<SaleItem>, returnedItems: Map<SaleItem, Double>, onItemSelected: (SaleItem, Double) -> Unit) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(saleItems) { saleItem ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = saleItem.productName)
                TextField(
                    value = returnedItems[saleItem]?.toString() ?: "",
                    onValueChange = { onItemSelected(saleItem, it.toDoubleOrNull() ?: 0.0) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}
