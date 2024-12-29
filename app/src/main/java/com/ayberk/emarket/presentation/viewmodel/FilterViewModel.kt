package com.ayberk.emarket.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayberk.emarket.data.model.Products
import com.ayberk.emarket.data.model.ProductsItem
import com.ayberk.emarket.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilterViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _brands = MutableLiveData<List<String>>()
    val brands: LiveData<List<String>> get() = _brands

    private val _models = MutableLiveData<List<String>>()
    val models: LiveData<List<String>> get() = _models

    fun loadBrands() {
        apiService.getProducts().enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                if (response.isSuccessful) {
                    val products = response.body()
                    val brands = products?.map { it.brand }?.distinct()
                    _brands.value = brands!!
                } else {
                    _errorMessage.value = response.errorBody()?.string() ?: "Veri alırken hata oluştu"
                }
            }

            override fun onFailure(call: Call<Products>, t: Throwable) {
                _errorMessage.value = t.message ?: "İnternet bağlantısı yok veya sunucuya ulaşılamıyor."
            }
        })
    }

    fun loadModels() {
        apiService.getProducts().enqueue(object : Callback<Products> {
            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                if (response.isSuccessful) {
                    val products = response.body()
                    val brands = products?.map { it.model }?.distinct()
                    _models.value = brands!!
                } else {
                    _errorMessage.value = response.errorBody()?.string() ?: "Veri alırken hata oluştu"
                }
            }

            override fun onFailure(call: Call<Products>, t: Throwable) {
                _errorMessage.value = t.message ?: "İnternet bağlantısı yok veya sunucuya ulaşılamıyor."
            }
        })
    }
}