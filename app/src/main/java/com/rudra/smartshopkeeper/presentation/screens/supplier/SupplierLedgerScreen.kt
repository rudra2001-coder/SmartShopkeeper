
package com.rudra.smartshopkeeper.presentation.screens.supplier

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.rudra.smartshopkeeper.data.database.entities.Purchase
import com.rudra.smartshopkeeper.data.database.entities.Supplier
import com.rudra.smartshopkeeper.presentation.components.BengaliText
import com.rudra.smartshopkeeper.presentation.viewmodels.SupplierLedgerViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierLedgerScreen(
    viewModel: SupplierLedgerViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val supplier by viewModel.supplier.collectAsState()
    val purchaseHistory by viewModel.purchaseHistory.collectAsState()
    var showAddBillDialog by remember { mutableStateOf(false) }
    var showAddPaymentDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showAddBillDialog) {
        AddBillDialog(
            onDismiss = { showAddBillDialog = false },
            onConfirm = {
                viewModel.addDue(it)
                showAddBillDialog = false
            }
        )
    }

    if (showAddPaymentDialog) {
        AddPaymentDialog(
            onDismiss = { showAddPaymentDialog = false },
            onConfirm = {
                viewModel.makePayment(it)
                showAddPaymentDialog = false
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFF1C2532),
        topBar = {
            TopAppBar(
                title = { BengaliText(text = "সরবরাহকারীর খাতা", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পিছনে", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.exportLedger(context) }) {
                        Icon(Icons.Default.Share, contentDescription = "এক্সপোর্ট", tint = Color(0xFF00D4AA))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (supplier == null) {
                CircularProgressIndicator(color = Color(0xFF00D4AA))
            } else {
                SupplierSummaryCard(supplier!!)

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { showAddBillDialog = true }, 
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))
                    ) {
                        BengaliText(text = "বিল যোগ", color = Color.White)
                    }
                    Button(
                        onClick = { showAddPaymentDialog = true }, 
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4AA))
                    ) {
                        BengaliText(text = "পেমেন্ট করুন", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                BengaliText(text = "ক্রয় ইতিহাস", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(purchaseHistory) { purchase ->
                        PurchaseListItem(purchase = purchase)
                    }
                }
            }
        }
    }
}

@Composable
fun SupplierSummaryCard(supplier: Supplier) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x402D3B50))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            BengaliText(text = supplier.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            BengaliText(text = "ফোন: ${supplier.phone ?: "N/A"}", color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Column(horizontalAlignment = Alignment.End) {
                    BengaliText(text = "মোট বকেয়া", fontSize = 14.sp, color = Color.Gray)
                    BengaliText(
                        text = "৳${supplier.totalDue}", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = if(supplier.totalDue > 0) Color(0xFFFF3B30) else Color(0xFF00D4AA)
                    )
                }
            }
        }
    }
}

@Composable
fun PurchaseListItem(purchase: Purchase) {
    val instant = Instant.fromEpochMilliseconds(purchase.date)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val monthName = dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    val date = "${dateTime.dayOfMonth} $monthName, ${dateTime.year}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x20FFFFFF))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                BengaliText(
                    text = "ক্রয়",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                BengaliText(text = date, fontSize = 12.sp, color = Color.Gray)
            }
            BengaliText(
                text = "৳${purchase.totalAmount}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00D4AA)
            )
        }
    }
}
