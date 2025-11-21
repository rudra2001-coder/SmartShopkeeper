
package com.rudra.smartshopkeeper.presentation.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.presentation.components.BengaliText
import com.rudra.smartshopkeeper.presentation.viewmodels.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToSales: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    val stats = listOf(
        StatData(
            "আজকের বিক্রয়",
            "৳ ${formatNumber(state.todaySales)}",
            "+৫.২% vs গতকাল",
            Icons.Default.PointOfSale,
            Color(0xFF00D4AA)
        ),
        StatData(
            "আজকের লাভ",
            "৳ ${formatNumber(state.todayProfit)}",
            "+৮.১% vs গতকাল",
            Icons.Default.AttachMoney,
            Color(0xFF3B82F6)
        ),
        StatData(
            "মোট বকেয়া",
            "৳ ${formatNumber(state.totalDue)}",
            "-১.৫% vs গত সপ্তাহ",
            Icons.Default.AccountBalanceWallet,
            Color(0xFFFF3B30)
        ),
        StatData(
            "আজকের খরচ",
            "৳ ${formatNumber(state.todayExpenses)}",
            "+২.৭% vs গতকাল",
            Icons.Default.Receipt,
            Color(0xFFFFA500)
        )
    )

    val premiumActions = listOf(
        PremiumAction("পণ্য", onNavigateToProducts, Icons.Default.Inventory2, Color(0xFF3B82F6)),
        PremiumAction("গ্রাহক", onNavigateToCustomers, Icons.Default.People, Color(0xFFFFA500)),
        PremiumAction("খরচ", onNavigateToExpenses, Icons.Default.Receipt, Color(0xFF9C27B0)),
        PremiumAction("রিপোর্ট", onNavigateToReports, Icons.Default.Analytics, Color(0xFF607D8B))
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF1C2532),
        floatingActionButton = {
            PremiumFAB(onClick = onNavigateToSales, icon = Icons.Default.AddShoppingCart)
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF00D4AA))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Header
                Spacer(modifier = Modifier.height(32.dp))
                DashboardHeader(modifier = Modifier.animateEnterance())

                // Stats Grid
                Spacer(modifier = Modifier.height(24.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    itemsIndexed(stats) { index, stat ->
                        GlassStatCard(
                            stat = stat,
                            modifier = Modifier.animateEnterance(delay = 200 + (index * 100))
                        )
                    }
                }

                // Quick Actions
                BengaliText(
                    text = "দ্রুত কাজসমূহ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .animateEnterance(delay = 600)
                )
                PremiumActionGrid(
                    actions = premiumActions,
                    modifier = Modifier.animateEnterance(delay = 700)
                )

                // Low Stock Alert
                if (state.lowStockProducts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    PremiumAlertCard(
                        title = "স্টক সতর্কতা",
                        subtitle = "${state.lowStockProducts.size}টি পণ্যের স্টক কম!",
                        products = state.lowStockProducts,
                        icon = Icons.Default.Warning,
                        gradient = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFF3B30), Color(0xFFFF9F0A))
                        ),
                        modifier = Modifier.animateEnterance(delay = 900)
                    )
                }

                Spacer(modifier = Modifier.height(100.dp)) // For FAB
            }
        }
        AnimatedBackgroundElements()
    }
}

// 👑 PREMIUM HEADER
@Composable
fun DashboardHeader(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                BengaliText(
                    text = "স্বাগতম!",
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
                    progress = { 0.7f },
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
                modifier = Modifier.animateEnterance(delay = 600 + (index * 100))
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
                    //maxLines = 2
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

data class DashboardState(
    val todaySales: Double = 0.0,
    val todayProfit: Double = 0.0,
    val todayExpenses: Double = 0.0,
    val totalDue: Double = 0.0,
    val lowStockProducts: List<com.rudra.smartshopkeeper.data.database.entities.Product> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

fun formatNumber(number: Double): String {
    return NumberFormat.getNumberInstance(Locale("bn", "BD")).format(number)
}
