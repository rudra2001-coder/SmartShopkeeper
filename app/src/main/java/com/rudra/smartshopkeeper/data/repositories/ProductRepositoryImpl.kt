
package com.rudra.smartshopkeeper.data.repositories

import com.rudra.smartshopkeeper.data.database.daos.ProductDao
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.domain.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository {
    override fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
    override suspend fun getProductById(id: String): Product? = productDao.getProductById(id)
    override fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)
    override suspend fun insertProduct(product: Product) = productDao.insertProduct(product)
    override suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    override suspend fun decreaseStock(productId: String, quantity: Double) = productDao.decreaseStock(productId, quantity)
    override suspend fun increaseStock(productId: String, quantity: Double) = productDao.increaseStock(productId, quantity)
    override fun getLowStockProducts(): Flow<List<Product>> = productDao.getLowStockProducts()
    override fun getExpiredProducts(date: Long): Flow<List<Product>> = productDao.getExpiredProducts(date)
}
