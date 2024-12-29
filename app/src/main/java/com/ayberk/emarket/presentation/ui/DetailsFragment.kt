package com.ayberk.emarket.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ayberk.emarket.R
import com.ayberk.emarket.data.model.CartItem
import com.ayberk.emarket.data.model.FavoriteItem
import com.ayberk.emarket.data.model.ProductsItem
import com.ayberk.emarket.data.room.AppDatabase
import com.ayberk.emarket.data.room.FavoriteDao
import com.ayberk.emarket.databinding.FragmentDetailsBinding
import com.ayberk.emarket.presentation.adapter.HomeProductsAdapter
import com.ayberk.emarket.presentation.viewmodel.CartViewModel
import com.ayberk.emarket.presentation.viewmodel.FavoriteViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var product: ProductsItem
    private lateinit var cartViewModel: CartViewModel
    private lateinit var adapter: HomeProductsAdapter
    private lateinit var favoriteDao: FavoriteDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        val appDatabase = AppDatabase.getInstance(requireContext())
        favoriteDao = appDatabase.favoriteDao()

        binding.progressBar.visibility = View.VISIBLE
        adapter = HomeProductsAdapter(
            products = emptyList(),
            onItemClicked = { },
            onAddToCartClicked = {  },
            onAddToFavoriClicked = { },
            favoriteDao
        )

        arguments?.let {
            product = it.getParcelable("product") ?: return@let
            setToolbar()
            displayProductDetails(product)
        }

        binding.btnAddCart.setOnClickListener {
            addToCart(product)
        }

        CoroutineScope(Dispatchers.Main).launch {
            val isFavorite = withContext(Dispatchers.IO) {
                favoriteDao.isFavorite(id = product.id)
            }
            if (isFavorite) {
                binding.starIcon.setImageResource(R.drawable.starenabled)
            } else {
                binding.starIcon.setImageResource(R.drawable.stardisable)
            }
        }

        binding.starIcon.setOnClickListener {

            handleFavoriteClick(product)
        }

        return binding.root
    }

    private fun handleFavoriteClick(product: ProductsItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val isFavorite = favoriteDao.isFavorite(product.id)
            withContext(Dispatchers.Main) {
                if (isFavorite) {
                    CoroutineScope(Dispatchers.IO).launch {
                        favoriteDao.deleteFavoriteItem(FavoriteItem(product.id, product.name, product.price, product.image))
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "${product.name} Favorilerden Silindi", Toast.LENGTH_SHORT).show()

                            binding.starIcon.setImageResource(R.drawable.stardisable)
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        favoriteDao.insertFavoriteItem(FavoriteItem(product.id, product.name, product.price, product.image))
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "${product.name} Favorilere Eklendi", Toast.LENGTH_SHORT).show()

                            binding.starIcon.setImageResource(R.drawable.starenabled)
                        }
                    }
                }
            }
        }
    }

    private fun addToCart(product: ProductsItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val exists = cartViewModel.isProductInCart(product.id)
            withContext(Dispatchers.Main) {
                if (exists) {
                    Toast.makeText(requireContext(), "Bu ürün zaten sepette.", Toast.LENGTH_SHORT).show()
                } else {
                    val cartItem = CartItem(
                        id = product.id,
                        name = product.name,
                        price = product.price,
                        quantity = 1,
                        totalPrice = null
                    )
                    cartViewModel.addToCart(cartItem)

                    Toast.makeText(requireContext(), "${product.name} sepete eklendi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayProductDetails(product: ProductsItem) {
        binding.productTitle.text = product.name
        binding.txtPrice.text = product.price + " ₺"
        binding.productDescription.text = product.description

        Glide.with(binding.productImage.context)
            .load(product.image)
            .error(R.drawable.loadingimage)
            .placeholder(R.drawable.loadingimage)
            .centerCrop()
            .into(binding.productImage)

        binding.progressBar.visibility = View.GONE
    }

    fun setToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.headers.toolbar)

        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.headers.toolbarTitle.text = product.name

        binding.headers.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}
