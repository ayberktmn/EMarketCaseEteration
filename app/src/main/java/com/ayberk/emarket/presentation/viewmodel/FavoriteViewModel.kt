package com.ayberk.emarket.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ayberk.emarket.data.model.FavoriteItem
import com.ayberk.emarket.data.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteDao = AppDatabase.getInstance(application).favoriteDao()

    private val _favoriteItemsLiveData = MutableLiveData<List<FavoriteItem>>()
    val favoriteItemsLiveData: LiveData<List<FavoriteItem>> = _favoriteItemsLiveData

    init {
        loadFavoriteItems()
    }

    private fun loadFavoriteItems() {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                favoriteDao.getAllFavoriteItems()
            }
            _favoriteItemsLiveData.postValue(items)
        }
    }

    fun addFavoriteItem(favoriteItem: FavoriteItem) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                favoriteDao.insertFavoriteItem(favoriteItem)
            }
            loadFavoriteItems()
        }
    }

    fun removeFavoriteItem(favoriteItem: FavoriteItem) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                favoriteDao.deleteFavoriteItem(favoriteItem)
            }
            loadFavoriteItems()
        }
    }
}
