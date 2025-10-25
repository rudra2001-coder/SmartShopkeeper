
package com.rudra.smartshopkeeper.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.smartshopkeeper.presentation.components.BengaliText
import com.rudra.smartshopkeeper.presentation.viewmodels.AddEditProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    viewModel: AddEditProductViewModel = hiltViewModel(),
    onBack: () -> Unit,
    productId: String?
) {
    val state = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.loadProduct(productId)
        }
    }

    // Show success/error messages
    LaunchedEffect(state.saveSuccess, state.errorMessage) {
        if (state.saveSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("পণ্য সফলভাবে সেভ হয়েছে!")
                onBack()
            }
        }
        state.errorMessage?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    BengaliText(
                        text = if (productId == null) "নতুন পণ্য যোগ করুন" else "পণ্য সম্পাদনা করুন",
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                BengaliText("লোড হচ্ছে...")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Product Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        BengaliText(
                            text = "পণ্যের তথ্য",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // Product Name
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { viewModel.onNameChange(it) },
                            label = { BengaliText("পণ্যের নাম *") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            },
                            isError = state.nameError != null,
                            supportingText = {
                                state.nameError?.let { error ->
                                    BengaliText(text = error, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bengali Name (Optional)
                        OutlinedTextField(
                            value = state.bengaliName,
                            onValueChange = { viewModel.onBengaliNameChange(it) },
                            label = { BengaliText("বাংলা নাম") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Dropdown
                        CategoryDropdown(
                            selectedCategory = state.category,
                            onCategoryChange = { viewModel.onCategoryChange(it) },
                            categories = state.availableCategories
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Unit
                        OutlinedTextField(
                            value = state.unit,
                            onValueChange = { viewModel.onUnitChange(it) },
                            label = { BengaliText("একক *") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Storage, contentDescription = null)
                            },
                            isError = state.unitError != null,
                            supportingText = {
                                state.unitError?.let { error ->
                                    BengaliText(text = error, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pricing & Stock Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        BengaliText(
                            text = "মূল্য ও স্টক",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // Cost Price
                        OutlinedTextField(
                            value = state.costPrice,
                            onValueChange = { viewModel.onCostPriceChange(it) },
                            label = { BengaliText("ক্রয় মূল্য") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.PriceCheck, contentDescription = null)
                            },
                            isError = state.costPriceError != null,
                            supportingText = {
                                state.costPriceError?.let { error ->
                                    BengaliText(text = error, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sale Price
                        OutlinedTextField(
                            value = state.salePrice,
                            onValueChange = { viewModel.onSalePriceChange(it) },
                            label = { BengaliText("বিক্রয় মূল্য *") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.PriceCheck, contentDescription = null)
                            },
                            isError = state.salePriceError != null,
                            supportingText = {
                                state.salePriceError?.let { error ->
                                    BengaliText(text = error, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stock Quantity
                        OutlinedTextField(
                            value = state.stockQty,
                            onValueChange = { viewModel.onStockQtyChange(it) },
                            label = { BengaliText("বর্তমান স্টক *") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Inventory, contentDescription = null)
                            },
                            isError = state.stockQtyError != null,
                            supportingText = {
                                state.stockQtyError?.let { error ->
                                    BengaliText(text = error, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Minimum Stock Alert
                        OutlinedTextField(
                            value = state.minStockAlert,
                            onValueChange = { viewModel.onMinStockAlertChange(it) },
                            label = { BengaliText("ন্যূনতম স্টক সতর্কতা") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Warning, contentDescription = null)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            singleLine = true
                        )

                        // Profit Calculation (if both prices are available)
                        if (state.costPrice.isNotEmpty() && state.salePrice.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val cost = state.costPrice.toDoubleOrNull() ?: 0.0
                            val sale = state.salePrice.toDoubleOrNull() ?: 0.0
                            val profit = sale - cost
                            val profitPercentage = if (cost > 0) (profit / cost) * 100 else 0.0
                            
                            BengaliText(
                                text = "আনুমানিক লাভ: ৳${String.format("%.2f", profit)} (${String.format("%.1f", profitPercentage)}%)",
                                fontSize = 14.sp,
                                color = if (profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = {
                        if (viewModel.validateForm()) {
                            viewModel.saveProduct()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BengaliText("সেভ হচ্ছে...")
                    } else {
                        BengaliText(
                            text = if (productId == null) "পণ্য সংরক্ষণ করুন" else "পরিবর্তনগুলি সংরক্ষণ করুন",
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    categories: List<String>
) {
    val expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value }
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = { },
            label = { BengaliText("ক্যাটাগরি") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { BengaliText(category) },
                    onClick = {
                        onCategoryChange(category)
                        expanded.value = false
                    }
                )
            }
        }
    }
}
