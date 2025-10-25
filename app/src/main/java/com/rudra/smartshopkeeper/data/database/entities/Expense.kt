
package com.rudra.smartshopkeeper.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val date: Long = System.currentTimeMillis(),
    val amount: Double,
    val category: String,
    val description: String,
    val paymentMethod: String = "নগদ",
    val createdAt: Long = System.currentTimeMillis()
)
