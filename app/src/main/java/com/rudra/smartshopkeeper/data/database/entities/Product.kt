
package com.rudra.smartshopkeeper.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val bengaliName: String? = null,
    val unit: String = "pcs",
    val salePrice: Double,
    val costPrice: Double = 0.0,
    val stockQty: Double = 0.0,
    val category: String = "সাধারণ",
    val minStockAlert: Double = 5.0,
    val barcode: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val expiryDate: Long? = null
)
