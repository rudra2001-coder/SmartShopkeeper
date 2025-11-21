
package com.rudra.smartshopkeeper.domain.repositories

import com.rudra.smartshopkeeper.data.database.entities.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    suspend fun getProductById(id: String): Product?
    fun searchProducts(query: String): Flow<List<Product>>
    suspend fun insertProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun decreaseStock(productId: String, quantity: Double)
    suspend fun increaseStock(productId: String, quantity: Double)
    fun getLowStockProducts(): Flow<List<Product>>
    fun getExpiredProducts(date: Long): Flow<List<Product>>
}
