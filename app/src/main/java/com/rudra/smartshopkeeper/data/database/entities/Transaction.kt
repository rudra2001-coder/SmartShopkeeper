
package com.rudra.smartshopkeeper.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: String,
    val amount: Double,
    val type: String, // "DUE" or "PAYMENT"
    val timestamp: Long
)
