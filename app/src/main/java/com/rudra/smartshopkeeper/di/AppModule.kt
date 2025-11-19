
package com.rudra.smartshopkeeper.di

import android.content.Context
import com.rudra.smartshopkeeper.data.database.AppDatabase
import com.rudra.smartshopkeeper.data.repositories.CustomerRepositoryImpl
import com.rudra.smartshopkeeper.data.repositories.ExpenseRepositoryImpl
import com.rudra.smartshopkeeper.data.repositories.ProductRepositoryImpl
import com.rudra.smartshopkeeper.data.repositories.PurchaseRepositoryImpl
import com.rudra.smartshopkeeper.data.repositories.SaleRepositoryImpl
import com.rudra.smartshopkeeper.data.repositories.SupplierRepositoryImpl
import com.rudra.smartshopkeeper.domain.repositories.CustomerRepository
import com.rudra.smartshopkeeper.domain.repositories.ExpenseRepository
import com.rudra.smartshopkeeper.domain.repositories.ProductRepository
import com.rudra.smartshopkeeper.domain.repositories.PurchaseRepository
import com.rudra.smartshopkeeper.domain.repositories.SaleRepository
import com.rudra.smartshopkeeper.domain.repositories.SupplierRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideProductDao(appDatabase: AppDatabase) = appDatabase.productDao()

    @Provides
    @Singleton
    fun provideSaleDao(appDatabase: AppDatabase) = appDatabase.saleDao()

    @Provides
    @Singleton
    fun provideCustomerDao(appDatabase: AppDatabase) = appDatabase.customerDao()

    @Provides
    @Singleton
    fun provideExpenseDao(appDatabase: AppDatabase) = appDatabase.expenseDao()

    @Provides
    @Singleton
    fun provideShopDao(appDatabase: AppDatabase) = appDatabase.shopDao()

    @Provides
    @Singleton
    fun provideTransactionDao(appDatabase: AppDatabase) = appDatabase.transactionDao()

    @Provides
    @Singleton
    fun provideSupplierDao(appDatabase: AppDatabase) = appDatabase.supplierDao()

    @Provides
    @Singleton
    fun providePurchaseDao(appDatabase: AppDatabase) = appDatabase.purchaseDao()

    @Provides
    @Singleton
    fun provideProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository = productRepositoryImpl

    @Provides
    @Singleton
    fun provideSaleRepository(saleRepositoryImpl: SaleRepositoryImpl): SaleRepository = saleRepositoryImpl

    @Provides
    @Singleton
    fun provideCustomerRepository(customerRepositoryImpl: CustomerRepositoryImpl): CustomerRepository = customerRepositoryImpl

    @Provides
    @Singleton
    fun provideExpenseRepository(expenseRepositoryImpl: ExpenseRepositoryImpl): ExpenseRepository = expenseRepositoryImpl

    @Provides
    @Singleton
    fun provideSupplierRepository(supplierRepositoryImpl: SupplierRepositoryImpl): SupplierRepository = supplierRepositoryImpl

    @Provides
    @Singleton
    fun providePurchaseRepository(purchaseRepositoryImpl: PurchaseRepositoryImpl): PurchaseRepository = purchaseRepositoryImpl
}
