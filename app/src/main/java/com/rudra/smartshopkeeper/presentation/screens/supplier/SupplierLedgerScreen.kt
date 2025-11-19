
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
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
    var showAddDueDialog by remember { mutableStateOf(false) }
    var showMakePaymentDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showAddDueDialog) {
        AddSupplierDueDialog(
            onDismiss = { showAddDueDialog = false },
            onConfirm = {
                viewModel.addDue(it)
                showAddDueDialog = false
            }
        )
    }

    if (showMakePaymentDialog) {
        MakeSupplierPaymentDialog(
            onDismiss = { showMakePaymentDialog = false },
            onConfirm = {
                viewModel.makePayment(it)
                showMakePaymentDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { BengaliText(text = "সরবরাহকারীর খাতা", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পিছনে")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.exportLedger(context) }) {
                        Icon(Icons.Default.Share, contentDescription = "এক্সপোর্ট", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                CircularProgressIndicator()
            } else {
                SupplierSummaryCard(supplier!!)

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { showAddDueDialog = true }, modifier = Modifier.weight(1f)) {
                        BengaliText(text = "বকেয়া যোগ")
                    }
                    Button(onClick = { showMakePaymentDialog = true }, modifier = Modifier.weight(1f)) {
                        BengaliText(text = "পেমেন্ট করুন")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                BengaliText(text = "ক্রয় ইতিহাস", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            BengaliText(text = supplier.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            BengaliText(text = "ফোন: ${supplier.phone ?: "N/A"}")
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Column {
                    BengaliText(text = "মোট বকেয়া", fontSize = 14.sp)
                    BengaliText(
                        text = "৳${supplier.totalDue}", 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = if(supplier.totalDue > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    fontSize = 16.sp
                )
                BengaliText(text = date, fontSize = 12.sp)
            }
            BengaliText(
                text = "৳${purchase.totalAmount}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
