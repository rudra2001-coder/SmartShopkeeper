
package com.rudra.smartshopkeeper.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val date: Long = System.currentTimeMillis(),
    val customerId: String? = null,
    val totalAmount: Double,
    val paidAmount: Double,
    val dueAmount: Double = 0.0,
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val paymentMethod: String = "নগদ",
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
