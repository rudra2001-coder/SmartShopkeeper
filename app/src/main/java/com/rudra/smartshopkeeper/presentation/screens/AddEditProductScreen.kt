
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
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
        containerColor = Color(0xFF1C2532),
        topBar = {
            TopAppBar(
                title = { 
                    BengaliText(
                        text = if (productId == null) "নতুন পণ্য" else "পণ্য সম্পাদনা",
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
                CircularProgressIndicator(color = Color(0xFF00D4AA))
                Spacer(modifier = Modifier.height(16.dp))
                BengaliText("লোড হচ্ছে...", color = Color.White)
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
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x402D3B50))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        BengaliText(
                            text = "পণ্যের তথ্য",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00D4AA)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // Product Name
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { viewModel.onNameChange(it) },
                            label = { BengaliText("পণ্যের নাম *", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Gray)
                            },
                            isError = state.nameError != null,
                            supportingText = {
                                state.nameError?.let { error ->
                                    BengaliText(text = error, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true,
                            colors = getTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bengali Name (Optional)
                        OutlinedTextField(
                            value = state.bengaliName,
                            onValueChange = { viewModel.onBengaliNameChange(it) },
                            label = { BengaliText("বাংলা নাম", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true,
                            colors = getTextFieldColors()
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
                            label = { BengaliText("একক *", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Storage, contentDescription = null, tint = Color.Gray)
                            },
                            isError = state.unitError != null,
                            supportingText = {
                                state.unitError?.let { error ->
                                    BengaliText(text = error, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true,
                            colors = getTextFieldColors()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pricing & Stock Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x402D3B50))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        BengaliText(
                            text = "মূল্য ও স্টক",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00D4AA)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // Cost Price
                        OutlinedTextField(
                            value = state.costPrice,
                            onValueChange = { viewModel.onCostPriceChange(it) },
                            label = { BengaliText("ক্রয় মূল্য", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.PriceCheck, contentDescription = null, tint = Color.Gray)
                            },
                            isError = state.costPriceError != null,
                            supportingText = {
                                state.costPriceError?.let { error ->
                                    BengaliText(text = error, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true,
                            colors = getTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sale Price
                        OutlinedTextField(
                            value = state.salePrice,
                            onValueChange = { viewModel.onSalePriceChange(it) },
                            label = { BengaliText("বিক্রয় মূল্য *", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.PriceCheck, contentDescription = null, tint = Color.Gray)
                            },
                            isError = state.salePriceError != null,
                            supportingText = {
                                state.salePriceError?.let { error ->
                                    BengaliText(text = error, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true,
                            colors = getTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stock Quantity
                        OutlinedTextField(
                            value = state.stockQty,
                            onValueChange = { viewModel.onStockQtyChange(it) },
                            label = { BengaliText("বর্তমান স্টক *", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Inventory, contentDescription = null, tint = Color.Gray)
                            },
                            isError = state.stockQtyError != null,
                            supportingText = {
                                state.stockQtyError?.let { error ->
                                    BengaliText(text = error, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            singleLine = true,
                            colors = getTextFieldColors()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Minimum Stock Alert
                        OutlinedTextField(
                            value = state.minStockAlert,
                            onValueChange = { viewModel.onMinStockAlertChange(it) },
                            label = { BengaliText("ন্যূনতম স্টক সতর্কতা", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Gray)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            singleLine = true,
                            colors = getTextFieldColors()
                        )

                        // Profit Calculation
                        if (state.costPrice.isNotEmpty() && state.salePrice.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val cost = state.costPrice.toDoubleOrNull() ?: 0.0
                            val sale = state.salePrice.toDoubleOrNull() ?: 0.0
                            val profit = sale - cost
                            val profitPercentage = if (cost > 0) (profit / cost) * 100 else 0.0
                            
                            BengaliText(
                                text = "আনুমানিক লাভ: ৳${String.format("%.2f", profit)} (${String.format("%.1f", profitPercentage)}%)",
                                fontSize = 14.sp,
                                color = if (profit >= 0) Color(0xFF00D4AA) else MaterialTheme.colorScheme.error
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
                    enabled = !state.isSaving,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4AA))
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BengaliText("সেভ হচ্ছে...", color = Color.White)
                    } else {
                        BengaliText(
                            text = if (productId == null) "পণ্য সংরক্ষণ করুন" else "পরিবর্তনগুলি সংরক্ষণ করুন",
                            fontSize = 16.sp, 
                            color = Color.White
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
            label = { BengaliText("ক্যাটাগরি", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            colors = getTextFieldColors()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun getTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Color(0xFF00D4AA),
    focusedIndicatorColor = Color(0xFF00D4AA),
    unfocusedIndicatorColor = Color.Gray,
    focusedContainerColor = Color(0x20FFFFFF),
    unfocusedContainerColor = Color(0x20FFFFFF)
)
