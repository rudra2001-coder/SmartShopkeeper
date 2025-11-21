package com.rudra.smartshopkeeper.presentation.screens.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.presentation.components.BengaliText
import com.rudra.smartshopkeeper.presentation.viewmodels.DashboardState
import com.rudra.smartshopkeeper.presentation.viewmodels.DashboardViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    // Format numbers for display
    val decimalFormat = remember { DecimalFormat("#,##0.00") }

    Scaffold(
        topBar = {
            GlassTopAppBar(
                onMenuClick = { showMenu = !showMenu },
                showMenu = showMenu,
                onSuppliersClick = {
                    onNavigateToSuppliers()
                    showMenu = false
                },
                onExpensesClick = {
                    onNavigateToExpenses()
                    showMenu = false
                },
                onAddPurchaseClick = {
                    onNavigateToAddPurchase()
                    showMenu = false
                },
                onSaleReturnClick = {
                    onNavigateToSaleReturn()
                    showMenu = false
                }
            )
        },
        floatingActionButton = {
            PremiumFAB(
                onClick = onNavigateToSales,
                icon = Icons.Default.AddShoppingCart
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F1B2D),
                            Color(0xFF1E2B3E),
                            Color(0xFF2D3B50)
                        )
                    )
                )
        ) {
            // Show loading or error states
            when {
                state.isLoading -> {
                    LoadingState()
                }
                state.error != null -> {
                    ErrorState(error = state.error!!)
                }
                else -> {
                    // Animated background elements
                    AnimatedBackgroundElements()

                    DashboardContent(
                        state = state,
                        decimalFormat = decimalFormat,
                        padding = padding,
                        onNavigateToSales = onNavigateToSales,
                        onNavigateToProducts = onNavigateToProducts,
                        onNavigateToCustomers = onNavigateToCustomers,
                        onNavigateToSuppliers = onNavigateToSuppliers,
                        onNavigateToExpenses = onNavigateToExpenses,
                        onNavigateToReports = onNavigateToReports,
                        onNavigateToAddPurchase = onNavigateToAddPurchase,
                        onNavigateToSaleReturn = onNavigateToSaleReturn
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardContent(
    state: DashboardState,
    decimalFormat: DecimalFormat,
    padding: PaddingValues,
    onNavigateToSales: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToSuppliers: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToAddPurchase: () -> Unit,
    onNavigateToSaleReturn: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        // Premium Welcome Section
        item {
            PremiumWelcomeSection(
                modifier = Modifier
                    .padding(24.dp)
                    .animateEnterance(delay = 0)
            )
        }

        // Glass Stats Cards
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .height(260.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(
                    items = listOf(
                        StatData(
                            "আজকের বিক্রয়",
                            "৳ ${decimalFormat.format(state.todaySales)}",
                            getSalesTrend(state.todaySales),
                            Icons.Default.PointOfSale,
                            Color(0xFF00D4AA)
                        ),
                        StatData(
                            "আজকের লাভ",
                            "৳ ${decimalFormat.format(state.todayProfit)}",
                            getProfitTrend(state.todayProfit),
                            Icons.Default.AttachMoney,
                            Color(0xFF6366F1)
                        ),
                        StatData(
                            "মোট বকেয়া",
                            "৳ ${decimalFormat.format(state.totalDue)}",
                            "↓",
                            Icons.Default.AccountBalanceWallet,
                            Color(0xFFF59E0B)
                        ),
                        StatData(
                            "আজকের খরচ",
                            "৳ ${decimalFormat.format(state.todayExpenses)}",
                            "→",
                            Icons.Default.Receipt,
                            Color(0xFFEF4444)
                        )
                    )
                ) { index, stat ->
                    GlassStatCard(
                        stat = stat,
                        modifier = Modifier.animateEnterance(delay = 100 + (index * 150))
                    )
                }
            }
        }

        // Quick Actions with Premium Design
        item {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .animateEnterance(delay = 400)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BengaliText(
                        text = "দ্রুত অ্যাকশন",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    PremiumBadge(count = state.quickActionCount)
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Main Action Cards
                PremiumActionGrid(
                    actions = listOf(
                        PremiumAction("নতুন বিক্রয়", onNavigateToSales, Icons.Default.AddShoppingCart, Color(0xFF00D4AA)),
                        PremiumAction("নতুন ক্রয়", onNavigateToAddPurchase, Icons.Default.ShoppingCart, Color(0xFF6366F1)),
                        PremiumAction("পণ্য তালিকা", onNavigateToProducts, Icons.Default.Inventory2, Color(0xFFF59E0B)),
                        PremiumAction("গ্রাহক ব্যবস্থাপনা", onNavigateToCustomers, Icons.Default.People, Color(0xFF8B5CF6))
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Secondary Actions
                PremiumActionRow(
                    actions = listOf(
                        PremiumAction("সরবরাহকারী", onNavigateToSuppliers, Icons.Default.LocalShipping, Color(0xFFEC4899)),
                        PremiumAction("রিপোর্ট", onNavigateToReports, Icons.Default.Analytics, Color(0xFF06B6D4)),
                        PremiumAction("খরচ", onNavigateToExpenses, Icons.Default.Receipt, Color(0xFF84CC16))
                    )
                )
            }
        }

        // Premium Alerts Section
        if (state.lowStockProducts.isNotEmpty() || state.expiredProducts.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .animateEnterance(delay = 800)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BengaliText(
                            text = "সিস্টেম সতর্কতা",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFF59E0B))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.lowStockProducts.isNotEmpty()) {
                        PremiumAlertCard(
                            title = "স্টক সতর্কতা",
                            subtitle = "${state.lowStockProducts.size}টি পণ্য কম স্টক এ",
                            products = state.lowStockProducts,
                            icon = Icons.Default.Warning,
                            gradient = Brush.linearGradient(
                                colors = listOf(Color(0xFFFF7B00), Color(0xFFFF5500))
                            ),
                            modifier = Modifier.animateEnterance(delay = 900)
                        )
                    }

                    if (state.expiredProducts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        PremiumAlertCard(
                            title = "মেয়াদোত্তীর্ণ পণ্য",
                            subtitle = "${state.expiredProducts.size}টি পণ্যের মেয়াদ শেষ",
                            products = state.expiredProducts,
                            icon = Icons.Default.Error,
                            gradient = Brush.linearGradient(
                                colors = listOf(Color(0xFFFF3B30), Color(0xFFFF2D55))
                            ),
                            modifier = Modifier.animateEnterance(delay = 1000)
                        )
                    }
                }
            }
        }
    }
}

// 🎨 PREMIUM GLASS TOP BAR
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTopAppBar(
    onMenuClick: () -> Unit,
    showMenu: Boolean,
    onSuppliersClick: () -> Unit,
    onExpensesClick: () -> Unit,
    onAddPurchaseClick: () -> Unit,
    onSaleReturnClick: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        color = Color(0x801E2B3E),
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x801E2B3E),
                        Color(0x401E2B3E)
                    )
                )
            )
            .blur(8.dp)
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Dashboard,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .padding(end = 12.dp)
                    )
                    BengaliText(
                        text = "ড্যাশবোর্ড",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            ),
            actions = {
                IconButton(onClick = { /* Notifications */ }) {
                    Box {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }

                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.scale(animateFloatAsState(if (showMenu) 1.1f else 1f).value)
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { onMenuClick() }
                ) {
                    DropdownMenuItem(
                        text = { BengaliText(text = "সরবরাহকারী") },
                        onClick = onSuppliersClick
                    )
                    DropdownMenuItem(
                        text = { BengaliText(text = "খরচ") },
                        onClick = onExpensesClick
                    )
                    DropdownMenuItem(
                        text = { BengaliText(text = "নতুন ক্রয়") },
                        onClick = onAddPurchaseClick
                    )
                    DropdownMenuItem(
                        text = { BengaliText(text = "পণ্য ফেরত") },
                        onClick = onSaleReturnClick
                    )
                }
            }
        )
    }
}

// 🌟 PREMIUM WELCOME SECTION
@Composable
fun PremiumWelcomeSection(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x402D3B50)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x406366F1),
                            Color(0x4000D4AA)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(28.dp)
            ) {
                BengaliText(
                    text = "স্বাগতম! 👋",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                BengaliText(
                    text = "আপনার ব্যবসার পারফরম্যান্স সারসংক্ষেপ",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = 0.7f,
                    color = Color(0xFF00D4AA),
                    trackColor = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

// 💎 GLASS STAT CARD
@Composable
fun GlassStatCard(stat: StatData, modifier: Modifier = Modifier) {
    var isHovered by remember { mutableStateOf(false) }
    val elevation = animateDpAsState(
        targetValue = if (isHovered) 16.dp else 8.dp,
        animationSpec = tween(300)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHovered = true
                        tryAwaitRelease()
                        isHovered = false
                    }
                )
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x402D3B50)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.value)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            stat.color.copy(alpha = 0.2f),
                            stat.color.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = stat.icon,
                        contentDescription = null,
                        tint = stat.color,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BengaliText(
                        text = stat.title,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                BengaliText(
                    text = stat.value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                BengaliText(
                    text = stat.trend,
                    fontSize = 11.sp,
                    color = stat.color,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// 🚀 PREMIUM ACTION GRID
@Composable
fun PremiumActionGrid(
    actions: List<PremiumAction>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.height(200.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(actions) { index, action ->
            PremiumActionCard(
                action = action,
                modifier = Modifier.animateEnterance(delay = 500 + (index * 100))
            )
        }
    }
}

@Composable
fun PremiumActionCard(action: PremiumAction, modifier: Modifier = Modifier) {
    var isPressed by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = SpringSpec(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow)
    )

    Card(
        onClick = action.onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
            .scale(scale.value)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x402D3B50)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            action.color.copy(alpha = 0.3f),
                            action.color.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = action.color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                BengaliText(
                    text = action.text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                   // maxLines = 2
                )
            }
        }
    }
}

// 🔥 PREMIUM FLOATING ACTION BUTTON
@Composable
fun PremiumFAB(onClick: () -> Unit, icon: ImageVector) {
    var isPressed by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = SpringSpec(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow)
    )

    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFF00D4AA),
        contentColor = Color.White,
        modifier = Modifier
            .scale(scale.value)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(icon, contentDescription = "New Sale", modifier = Modifier.size(24.dp))
    }
}

// 🎯 PREMIUM BADGE
@Composable
fun PremiumBadge(count: Int) {
    Box(
        modifier = Modifier
            .background(
                color = Color(0xFFFF3B30),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        BengaliText(
            text = "$count টি",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// ⚡ PREMIUM ALERT CARD
@Composable
fun PremiumAlertCard(
    title: String,
    subtitle: String,
    products: List<Product>,
    icon: ImageVector,
    gradient: Brush,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x402D3B50)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BengaliText(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                BengaliText(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                products.take(2).forEach { product ->
                    BengaliText(
                        text = "• ${product.name} - ${product.stockQty} ${product.unit}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                if (products.size > 2) {
                    BengaliText(
                        text = "এবও ${products.size - 2}টি পণ্য...",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

// ✨ ANIMATED BACKGROUND ELEMENTS
@Composable
fun AnimatedBackgroundElements() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                onDrawBehind {
                    drawCircle(
                        color = Color(0x156366F1),
                        radius = 150f,
                        center = Offset(size.width * 0.8f, size.height * 0.2f)
                    )
                    drawCircle(
                        color = Color(0x1500D4AA),
                        radius = 100f,
                        center = Offset(size.width * 0.2f, size.height * 0.7f)
                    )
                }
            }
    )
}

// 🎭 ENTRANCE ANIMATION MODIFIER
@Composable
fun Modifier.animateEnterance(delay: Int = 0): Modifier {
    var isVisible by remember { mutableStateOf(false) }
    val alpha = animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = delay)
    )
    val offsetY = animateDpAsState(
        targetValue = if (isVisible) 0.dp else 30.dp,
        animationSpec = tween(durationMillis = 800, delayMillis = delay)
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    return this
        .offset(y = offsetY.value)
        .alpha(alpha.value)
}

// 🎯 PREMIUM ACTION ROW
@Composable
fun PremiumActionRow(actions: List<PremiumAction>, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        actions.forEachIndexed { index, action ->
            PremiumActionCard(
                action = action,
                modifier = Modifier
                    .weight(1f)
                    .animateEnterance(delay = 700 + (index * 100))
            )
        }
    }
}

// 📊 LOADING STATE
@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1B2D),
                        Color(0xFF1E2B3E),
                        Color(0xFF2D3B50)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LinearProgressIndicator(
                color = Color(0xFF00D4AA),
                modifier = Modifier.width(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            BengaliText(
                text = "ডেটা লোড হচ্ছে...",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

// ❌ ERROR STATE
@Composable
fun ErrorState(error: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1B2D),
                        Color(0xFF1E2B3E),
                        Color(0xFF2D3B50)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Error,
                contentDescription = "Error",
                tint = Color(0xFFFF3B30),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            BengaliText(
                text = "ত্রুটি: $error",
                fontSize = 16.sp,
                color = Color.White,
              //  textAlign = TextAlign.Center
            )
        }
    }
}

// Helper functions for trend indicators
private fun getSalesTrend(sales: Double): String {
    return if (sales > 1000) "↑↑" else if (sales > 500) "↑" else "→"
}

private fun getProfitTrend(profit: Double): String {
    return if (profit > 200) "↑↑" else if (profit > 100) "↑" else if (profit < 0) "↓" else "→"
}

// DATA CLASSES
data class StatData(
    val title: String,
    val value: String,
    val trend: String,
    val icon: ImageVector,
    val color: Color
)

data class PremiumAction(
    val text: String,
    val onClick: () -> Unit,
    val icon: ImageVector,
    val color: Color
)