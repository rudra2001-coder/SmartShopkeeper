
package com.rudra.smartshopkeeper.presentation.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.presentation.components.BengaliText
import com.rudra.smartshopkeeper.presentation.components.BigActionButton
import com.rudra.smartshopkeeper.presentation.components.StatCard
import com.rudra.smartshopkeeper.presentation.viewmodels.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToSales: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToSuppliers: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToAddPurchase: () -> Unit,
    onNavigateToSaleReturn: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BengaliText(
                        text = "ড্যাশবোর্ড",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        BengaliText(
                            text = "স্বাগতম!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        BengaliText(
                            text = "আজকের সারসংক্ষেপ",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                 LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(horizontal = 16.dp).height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { 
                        StatCard(
                            title = "আজকের বিক্রয়",
                            value = "৳ ${state.todaySales}",
                            icon = Icons.Default.PointOfSale,
                            valueColor = Color(0xFF006A4E)
                        )
                    }
                    item {
                        StatCard(
                            title = "আজকের লাভ",
                            value = "৳ ${state.todayProfit}",
                            icon = Icons.Default.AttachMoney,
                            valueColor = Color(0xFF2196F3)
                        )
                    }
                    item {
                        StatCard(
                            title = "মোট বকেয়া",
                            value = "৳ ${state.totalDue}",
                            icon = Icons.Default.AccountBalanceWallet,
                            valueColor = Color(0xFFF42A41)
                        )
                    }
                    item {
                        StatCard(
                            title = "আজকের খরচ",
                            value = "৳ ${state.todayExpenses}",
                            icon = Icons.Default.Receipt,
                            valueColor = Color(0xFFFF9800)
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    BengaliText(
                        text = "দ্রুত কাজসমূহ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BigActionButton(
                            text = "নতুন বিক্রয়",
                            onClick = onNavigateToSales,
                            icon = Icons.Default.AddShoppingCart
                        )
                        
                        BigActionButton(
                            text = "নতুন ক্রয়",
                            onClick = onNavigateToAddPurchase,
                            icon = Icons.Default.ShoppingCart
                        )

                        BigActionButton(
                            text = "পণ্য ফেরত",
                            onClick = onNavigateToSaleReturn,
                            icon = Icons.Default.AssignmentReturn
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BigActionButton(
                                text = "পণ্য",
                                onClick = onNavigateToProducts,
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Inventory2,
                                backgroundColor = Color(0xFF2196F3)
                            )
                            BigActionButton(
                                text = "গ্রাহক",
                                onClick = onNavigateToCustomers,
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.People,
                                backgroundColor = Color(0xFFFF9800)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                             BigActionButton(
                                text = "সরবরাহকারী",
                                onClick = onNavigateToSuppliers,
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.LocalShipping,
                                backgroundColor = Color(0xFF4CAF50)
                            )
                             BigActionButton(
                                text = "খরচ",
                                onClick = onNavigateToExpenses,
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Receipt,
                                backgroundColor = Color(0xFF9C27B0)
                            )
                        }
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                           
                            BigActionButton(
                                text = "রিপোর্ট",
                                onClick = onNavigateToReports,
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Analytics,
                                backgroundColor = Color(0xFF607D8B)
                            )
                        }
                    }
                }
            }
            
            if (state.lowStockProducts.isNotEmpty()) {
                item {
                    LowStockAlertSection(products = state.lowStockProducts)
                }
            }

            if (state.expiredProducts.isNotEmpty()) {
                item {
                    ExpiredProductAlertSection(products = state.expiredProducts)
                }
            }
        }
    }
}

@Composable
fun LowStockAlertSection(products: List<Product>) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        ),
        border = BorderStroke(1.dp, Color(0xFFFFB74D))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BengaliText(
                    text = "স্টক সতর্কতা",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9800)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            products.take(3).forEach { product ->
                BengaliText(
                    text = "• ${product.name} - ${product.stockQty} ${product.unit}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (products.size > 3) {
                BengaliText(
                    text = "এবং আরও ${products.size - 3}টি পণ্য...",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ExpiredProductAlertSection(products: List<Product>) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        border = BorderStroke(1.dp, Color(0xFFF44336))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BengaliText(
                    text = "মেয়াদোত্তীর্ণ পণ্য",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            products.take(3).forEach { product ->
                BengaliText(
                    text = "• ${product.name}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (products.size > 3) {
                BengaliText(
                    text = "এবং আরও ${products.size - 3}টি পণ্য...",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
