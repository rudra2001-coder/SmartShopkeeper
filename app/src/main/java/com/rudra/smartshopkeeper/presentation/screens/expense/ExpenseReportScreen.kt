
package com.rudra.smartshopkeeper.presentation.screens.expense

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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.smartshopkeeper.presentation.components.BengaliText
import com.rudra.smartshopkeeper.presentation.viewmodels.ExpenseReportViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseReportScreen(
    viewModel: ExpenseReportViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToExpenseVsIncome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { BengaliText(text = "খরচ রিপোর্ট", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পিছনে")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToExpenseVsIncome) {
                        Icon(Icons.Default.BarChart, contentDescription = "আয়-ব্যয়ের চার্ট", tint = MaterialTheme.colorScheme.onPrimary)
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
            DateRangeSelector(
                startDate = dateFormatter.format(Date(state.startDate)),
                endDate = dateFormatter.format(Date(state.endDate)),
                onStartDateSelect = { showStartDatePicker = true },
                onEndDateSelect = { showEndDatePicker = true },
                onGenerateReport = { viewModel.loadExpenseReport() }
            )

            if (showStartDatePicker) {
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.startDate)
                DatePickerDialog(
                    onDismissRequest = { showStartDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { 
                            datePickerState.selectedDateMillis?.let { viewModel.setStartDate(it) }
                            showStartDatePicker = false
                        }) {
                            Text("OK")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (showEndDatePicker) {
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.endDate)
                DatePickerDialog(
                    onDismissRequest = { showEndDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { 
                            datePickerState.selectedDateMillis?.let { viewModel.setEndDate(it) }
                            showEndDatePicker = false 
                        }) {
                            Text("OK")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.expensesByCategory.keys.toList()) { category ->
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        BengaliText(text = category, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        state.expensesByCategory[category]?.forEach { expense ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                BengaliText(text = expense.description, modifier = Modifier.weight(1f))
                                BengaliText(text = "৳${expense.amount}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateRangeSelector(startDate: String, endDate: String, onStartDateSelect: () -> Unit, onEndDateSelect: () -> Unit, onGenerateReport: () -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onStartDateSelect, modifier = Modifier.weight(1f)) {
                BengaliText(text = "শুরুর তারিখ: $startDate")
            }
            Button(onClick = onEndDateSelect, modifier = Modifier.weight(1f)) {
                BengaliText(text = "শেষ তারিখ: $endDate")
            }
        }
        Button(onClick = onGenerateReport, modifier = Modifier.fillMaxWidth()) {
            BengaliText(text = "রিপোর্ট দেখুন")
        }
    }
}
