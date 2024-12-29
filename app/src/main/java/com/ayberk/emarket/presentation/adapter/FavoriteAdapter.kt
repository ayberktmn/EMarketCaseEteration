package com.ayberk.emarket.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayberk.emarket.R
import com.ayberk.emarket.data.model.FavoriteItem
import com.ayberk.emarket.databinding.ItemFavoriBinding
import com.bumptech.glide.Glide

class FavoriteAdapter(private val onDeleteClick: (FavoriteItem) -> Unit) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var favoriteItems: List<FavoriteItem> = listOf()

    class FavoriteViewHolder(private val binding: ItemFavoriBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favoriteItem: FavoriteItem, onDeleteClick: (FavoriteItem) -> Unit) {
            binding.productName.text = favoriteItem.name
            binding.priceText.text = favoriteItem.price + " ₺"
            Glide.with(binding.productImage.context)
                .load(favoriteItem.imageUrl)
                .error(R.drawable.loadingimage)
                .placeholder(R.drawable.loadingimage)
                .centerCrop()
                .into(binding.productImage)

            binding.imgStar.setOnClickListener {
                onDeleteClick(favoriteItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteItem = favoriteItems[position]
        holder.bind(favoriteItem, onDeleteClick)
    }

    override fun getItemCount(): Int = favoriteItems.size

    fun updateFavorites(newFavorites: List<FavoriteItem>) {
        favoriteItems = newFavorites
        notifyDataSetChanged()
    }
}