
package com.rudra.smartshopkeeper.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val amount: Double,
    val category: String,
    val description: String,
    val paymentMethod: String = "নগদ",
    val createdAt: Long = System.currentTimeMillis()
)
