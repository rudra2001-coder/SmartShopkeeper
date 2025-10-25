
package com.rudra.smartshopkeeper.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop")
data class Shop(
    @PrimaryKey val id: Int = 1,
    val name: String = "আমার দোকান",
    val address: String = "",
    val phone: String = "",
    val currency: String = "৳",
    val taxRate: Double = 0.0,
    val isTaxInclusive: Boolean = false,
    val language: String = "bn",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
