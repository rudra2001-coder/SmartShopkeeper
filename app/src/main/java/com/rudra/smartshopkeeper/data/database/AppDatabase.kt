
package com.rudra.smartshopkeeper.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rudra.smartshopkeeper.data.database.daos.CustomerDao
import com.rudra.smartshopkeeper.data.database.daos.ExpenseDao
import com.rudra.smartshopkeeper.data.database.daos.ProductDao
import com.rudra.smartshopkeeper.data.database.daos.PurchaseDao
import com.rudra.smartshopkeeper.data.database.daos.SaleDao
import com.rudra.smartshopkeeper.data.database.daos.ShopDao
import com.rudra.smartshopkeeper.data.database.daos.SupplierDao
import com.rudra.smartshopkeeper.data.database.daos.TransactionDao
import com.rudra.smartshopkeeper.data.database.entities.Customer
import com.rudra.smartshopkeeper.data.database.entities.Expense
import com.rudra.smartshopkeeper.data.database.entities.Product
import com.rudra.smartshopkeeper.data.database.entities.Purchase
import com.rudra.smartshopkeeper.data.database.entities.PurchaseItem
import com.rudra.smartshopkeeper.data.database.entities.Sale
import com.rudra.smartshopkeeper.data.database.entities.SaleItem
import com.rudra.smartshopkeeper.data.database.entities.Shop
import com.rudra.smartshopkeeper.data.database.entities.Supplier
import com.rudra.smartshopkeeper.data.database.entities.Transaction

@Database(
    entities = [
        Product::class,
        Customer::class,
        Sale::class,
        SaleItem::class,
        Expense::class,
        Shop::class,
        Transaction::class,
        Supplier::class,
        Purchase::class,
        PurchaseItem::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun customerDao(): CustomerDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun shopDao(): ShopDao
    abstract fun transactionDao(): TransactionDao
    abstract fun supplierDao(): SupplierDao
    abstract fun purchaseDao(): PurchaseDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "smart_shopkeeper.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { Instance = it }
            }
        }
    }
}
