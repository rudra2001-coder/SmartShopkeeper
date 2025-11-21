
package com.rudra.smartshopkeeper.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val supplierId: String? = null,
    val totalAmount: Double,
    val date: Long
)
