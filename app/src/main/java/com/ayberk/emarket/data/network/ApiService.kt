package com.ayberk.emarket.data.network

import com.ayberk.emarket.data.model.Products
import com.ayberk.emarket.data.model.ProductsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("products")
    fun getProducts(): Call<Products>
}
