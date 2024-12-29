package com.ayberk.emarket.data.repository

import androidx.lifecycle.LiveData
import com.ayberk.emarket.data.room.CartDao

class CartRepository(private val cartDao: CartDao) {

    // LiveData döndüren metod
    fun getCartItemCountLiveData(): LiveData<Int> {
        return cartDao.getCartItemCount()
    }
}