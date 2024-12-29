package com.ayberk.emarket.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ayberk.emarket.data.model.FavoriteItem

@Dao
interface FavoriteDao {

    @Insert
    suspend fun insertFavoriteItem(favoriteItem: FavoriteItem)

    @Delete
    suspend fun deleteFavoriteItem(favoriteItem: FavoriteItem)

    @Query("SELECT * FROM favorite_items")
    suspend fun getAllFavoriteItems(): List<FavoriteItem>

    @Query("SELECT COUNT(*) FROM  favorite_items WHERE id = :id")
    suspend fun isFavorite(id: String): Boolean
}
