package com.rudra.smartshopkeeper.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rudra.smartshopkeeper.data.database.entities.Customer

@Composable
fun CustomerSelectionDialog(
    customers: List<Customer>,
    onCustomerSelected: (Customer) -> Unit,
    onDismiss: () -> Unit,
    onAddNewCustomer: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCustomers = if (searchQuery.isBlank()) {
        customers
    } else {
        customers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.phone?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { BengaliText(text = "গ্রাহক নির্বাচন করুন") },
        text = {
            Column {
                BengaliSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "গ্রাহকের নাম বা ফোন নম্বর"
                )
                LazyColumn {
                    items(filteredCustomers) { customer ->
                        Text(
                            text = "${customer.name} - ${customer.phone ?: ""}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCustomerSelected(customer) }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onAddNewCustomer) {
                BengaliText("নতুন গ্রাহক")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                BengaliText("বাতিল")
            }
        }
    )
}