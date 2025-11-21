
package com.rudra.smartshopkeeper.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String? = null,
    val address: String? = null,
    val totalDue: Double = 0.0
)
