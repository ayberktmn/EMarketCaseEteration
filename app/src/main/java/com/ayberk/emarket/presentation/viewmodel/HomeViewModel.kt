package com.ayberk.emarket.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayberk.emarket.data.model.Products
import com.ayberk.emarket.data.model.ProductsItem
import com.ayberk.emarket.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    private val _products = MutableLiveData<List<ProductsItem>>()
    val products: LiveData<List<ProductsItem>> get() = _products

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        _loading.value = true
        apiService.getProducts().enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                _loading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _products.value = response.body()
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = response.errorBody()?.string() ?: "Veri alırken hata oluştu"
                }
            }

            override fun onFailure(call: Call<Products>, t: Throwable) {
                _loading.value = false
                _errorMessage.value = t.message ?: "İnternet bağlantısı yok veya sunucuya ulaşılamıyor."
            }
        })
    }
}
