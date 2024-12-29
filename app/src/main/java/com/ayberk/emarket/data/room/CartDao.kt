package com.ayberk.emarket.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ayberk.emarket.data.model.CartItem

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Query("SELECT * FROM cart")
    fun getAllCartItemsLiveData(): List<CartItem>

    @Query("SELECT COUNT(*) FROM cart")
     fun getCartItemCount(): LiveData<Int>

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart")
    suspend fun clearCart()

    @Update
    suspend fun updateCartItems(cartItems: List<CartItem>)

    @Query("SELECT EXISTS(SELECT 1 FROM cart WHERE id = :productId LIMIT 1)")
    suspend fun checkIfProductInCart(productId: String): Boolean

}
