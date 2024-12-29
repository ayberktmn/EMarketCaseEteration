package com.ayberk.emarket.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ayberk.emarket.data.model.CartItem
import com.ayberk.emarket.data.repository.CartRepository
import com.ayberk.emarket.data.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartDao = AppDatabase.getInstance(application).cartDao()

    private val _favoriteCartsLiveData = MutableLiveData<List<CartItem>>()
    val favoriteCartsLiveData: LiveData<List<CartItem>> = _favoriteCartsLiveData

    private val _totalPriceLiveData = MutableLiveData<Double>()
    val totalPriceLiveData: LiveData<Double> = _totalPriceLiveData

    private val _cartItemCount = MutableLiveData<Int>()
    val cartItemCount: LiveData<Int> get() = _cartItemCount

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val repository: CartRepository

    private val cartItems = mutableListOf<CartItem>()


    init {
        val cartDao = AppDatabase.getInstance(application).cartDao()
        repository = CartRepository(cartDao)
        getAllCartItems()
    }

    fun observeCartItemCount() {
        repository.getCartItemCountLiveData().observeForever { count ->
            _cartItemCount.postValue(count)
        }
    }

    suspend fun isProductInCart(productId: String): Boolean {
        return cartDao.checkIfProductInCart(productId)
    }

    private fun getAllCartItems() {
        _loading.value = true
        viewModelScope.launch {
            val cart = withContext(Dispatchers.IO) {
                cartDao.getAllCartItemsLiveData()
            }
            cartItems.clear()
            cartItems.addAll(cart)
            _favoriteCartsLiveData.postValue(cartItems)
            calculateTotalPrice()
            _loading.value = false
        }
    }

    fun addToCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartDao.insertCartItem(cartItem)
            getAllCartItems()
        }
    }

    fun increaseQuantity(cartItem: CartItem) {
        updateQuantity(cartItem, increase = true)
    }

    fun decreaseQuantity(cartItem: CartItem) {
        updateQuantity(cartItem, increase = false)
    }

    private fun updateQuantity(cartItem: CartItem, increase: Boolean) {
        val index = cartItems.indexOf(cartItem)
        if (index != -1) {
            if (increase) {
                cartItems[index].quantity++
            } else if (cartItems[index].quantity > 1) {
                cartItems[index].quantity--
            }

            viewModelScope.launch(Dispatchers.IO) {
                cartDao.updateCartItems(listOf(cartItems[index]))
            }

            _favoriteCartsLiveData.postValue(cartItems)

            calculateTotalPrice()
        }
    }

    private fun calculateTotalPrice() {
        var totalPrice = 0.0
        for (cartItem in cartItems) {
            totalPrice += cartItem.quantity.toDouble() * cartItem.price.toDouble()
        }
        _totalPriceLiveData.postValue(totalPrice)
    }

    fun deleteCartItem(cartItem: CartItem) {
        viewModelScope.launch {
            cartDao.deleteCartItem(cartItem)
            getAllCartItems()
        }
    }
}
