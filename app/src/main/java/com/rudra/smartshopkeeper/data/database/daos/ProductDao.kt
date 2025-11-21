
package com.rudra.smartshopkeeper.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rudra.smartshopkeeper.data.database.entities.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name")
    fun getAllProducts(): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): Product?
    
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR bengaliName LIKE '%' || :query || '%' AND isActive = 1")
    fun searchProducts(query: String): Flow<List<Product>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)
    
    @Update
    suspend fun updateProduct(product: Product)
    
    @Query("UPDATE products SET stockQty = stockQty - :quantity WHERE id = :productId")
    suspend fun decreaseStock(productId: String, quantity: Double)
    
    @Query("UPDATE products SET stockQty = stockQty + :quantity WHERE id = :productId")
    suspend fun increaseStock(productId: String, quantity: Double)
    
    @Query("SELECT * FROM products WHERE stockQty <= minStockAlert AND isActive = 1")
    fun getLowStockProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE expiryDate IS NOT NULL AND expiryDate <= :date")
    fun getExpiredProducts(date: Long): Flow<List<Product>>
}
