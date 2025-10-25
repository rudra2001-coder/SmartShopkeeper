
package com.rudra.smartshopkeeper.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rudra.smartshopkeeper.data.database.entities.Shop

@Dao
interface ShopDao {
    @Query("SELECT * FROM shop WHERE id = 1")
    suspend fun getShop(): Shop?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: Shop)
    
    @Update
    suspend fun updateShop(shop: Shop)
}
