
package com.rudra.smartshopkeeper.presentation.screens.supplier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.rudra.smartshopkeeper.presentation.components.BengaliText

@Composable
fun MakeSupplierPaymentDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { BengaliText(text = "পেমেন্ট করুন") },
        text = {
            Column {
                BengaliText(text = "টাকার পরিমাণ লিখুন")
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val paymentAmount = amount.toDoubleOrNull()
                    if (paymentAmount != null) {
                        onConfirm(paymentAmount)
                    }
                }
            ) {
                BengaliText("নিশ্চিত")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                BengaliText("বাতিল")
            }
        }
    )
}
