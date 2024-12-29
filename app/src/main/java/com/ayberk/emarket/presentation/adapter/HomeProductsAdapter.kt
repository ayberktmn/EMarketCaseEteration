package com.ayberk.emarket.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayberk.emarket.R
import com.ayberk.emarket.databinding.ItemProductsBinding
import com.ayberk.emarket.data.model.ProductsItem
import com.bumptech.glide.Glide
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ayberk.emarket.data.room.FavoriteDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeProductsAdapter(
    private var products: List<ProductsItem>,
    private val onItemClicked: (ProductsItem) -> Unit,
    private val onAddToCartClicked: (ProductsItem) -> Unit,
    private val onAddToFavoriClicked: (ProductsItem) -> Unit,
    private var favoriteDao: FavoriteDao
) : RecyclerView.Adapter<HomeProductsAdapter.ProductViewHolder>(), Filterable {

    var filteredProducts: List<ProductsItem> = products

    fun updateProducts(newProducts: List<ProductsItem>) {
        products = newProducts
        filteredProducts = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductsBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding, onItemClicked, onAddToCartClicked, onAddToFavoriClicked,favoriteDao)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = filteredProducts[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = filteredProducts.size

    class ProductViewHolder(
        private val binding: ItemProductsBinding,
        private val onItemClicked: (ProductsItem) -> Unit,
        private val onAddToCartClicked: (ProductsItem) -> Unit,
        private val onAddToFavoriClicked: (ProductsItem) -> Unit,
        private var favoriteDao: FavoriteDao
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductsItem) {
            binding.root.setOnClickListener {
                onItemClicked(product)
            }

            binding.addToCartButton.setOnClickListener {
                onAddToCartClicked(product)
            }

            binding.imgStar.setOnClickListener {
                onAddToFavoriClicked(product)
            }

            binding.productName.text = product.name
            binding.priceText.text = "${product.price} ₺"
            Glide.with(binding.productImage.context)
                .load(product.image)
                .error(R.drawable.loadingimage)
                .placeholder(R.drawable.loadingimage)
                .centerCrop()
                .into(binding.productImage)

            CoroutineScope(Dispatchers.Main).launch {
                val isFavorite = withContext(Dispatchers.IO) {
                    favoriteDao.isFavorite(id = product.id)
                }
                if (isFavorite) {
                    binding.imgStar.setImageResource(R.drawable.starenabled)
                } else {
                    binding.imgStar.setImageResource(R.drawable.stardisable)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim() ?: ""
                val filteredList = if (query.isEmpty()) {
                    products
                } else {
                    products.filter {
                        it.name.contains(query, ignoreCase = true) || it.model.contains(query, ignoreCase = true)
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredProducts = results?.values as List<ProductsItem>
                notifyDataSetChanged()
            }
        }
    }
}