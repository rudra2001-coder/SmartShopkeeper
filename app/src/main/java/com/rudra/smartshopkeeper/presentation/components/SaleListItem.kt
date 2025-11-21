
package com.rudra.smartshopkeeper.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rudra.smartshopkeeper.data.database.entities.Sale
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun SaleListItem(sale: Sale, onClick: () -> Unit) {
    val instant = Instant.fromEpochMilliseconds(sale.date)
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
                    text = "ইনভয়েস: ${sale.invoiceNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                BengaliText(text = date, fontSize = 12.sp)
            }
            BengaliText(
                text = "৳${sale.totalAmount}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
