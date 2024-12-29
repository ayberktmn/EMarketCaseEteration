package com.ayberk.emarket.presentation.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.RoomDatabase
import com.ayberk.emarket.R
import com.ayberk.emarket.data.model.CartItem
import com.ayberk.emarket.data.model.FavoriteItem
import com.ayberk.emarket.databinding.FragmentHomeBinding
import com.ayberk.emarket.presentation.adapter.HomeProductsAdapter
import com.ayberk.emarket.presentation.viewmodel.HomeViewModel
import com.ayberk.emarket.data.model.ProductsItem
import com.ayberk.emarket.data.room.AppDatabase
import com.ayberk.emarket.data.room.CartDao
import com.ayberk.emarket.data.room.FavoriteDao
import com.ayberk.emarket.presentation.viewmodel.CartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var productViewModel: HomeViewModel
    private lateinit var adapter: HomeProductsAdapter
    private lateinit var cartViewModel: CartViewModel
    private lateinit var favoriteDao: FavoriteDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        productViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        val appDatabase = AppDatabase.getInstance(requireContext())
        favoriteDao = appDatabase.favoriteDao()

        setupToolbar()
        setupRecyclerView()
        setupObservers()

        productViewModel.fetchProducts()

        binding.searchAutoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                charSequence?.let { adapter.filter.filter(it) }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnApplyFilters.setOnClickListener {
            findNavController().navigate(R.id.filterFragment)
        }
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.headers.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.headers.toolbar.title = "E-Market"
        binding.headers.backButton.visibility = View.GONE
        binding.headers.toolbarTitle.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        adapter = HomeProductsAdapter(
            products = emptyList(),
            onItemClicked = { product -> navigateToDetailsFragment(product) },
            onAddToCartClicked = { product -> addToCart(product) },
            onAddToFavoriClicked = { product -> handleFavoriteClick(product) },
            favoriteDao
        )
        binding.recyclerProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerProducts.adapter = adapter
    }

    private fun handleFavoriteClick(product: ProductsItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val isFavorite = favoriteDao.isFavorite(product.id)
            withContext(Dispatchers.Main) {
                if (isFavorite) {
                    CoroutineScope(Dispatchers.IO).launch {
                        favoriteDao.deleteFavoriteItem(FavoriteItem(product.id, product.name, product.price, product.image))
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "${product.name} removed from favorites", Toast.LENGTH_SHORT).show()
                            updateFavoriteIcon(product)
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        favoriteDao.insertFavoriteItem(FavoriteItem(product.id, product.name, product.price, product.image))
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "${product.name} added to favorites", Toast.LENGTH_SHORT).show()
                            updateFavoriteIcon(product)
                        }
                    }
                }
            }
        }
    }

    private fun updateFavoriteIcon(product: ProductsItem) {
        val position = adapter.filteredProducts.indexOf(product)
        if (position != -1) {
            adapter.notifyItemChanged(position)
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

    private fun setupObservers() {
        productViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        productViewModel.products.observe(viewLifecycleOwner) { products ->
            if (products != null && products.isNotEmpty()) {
                adapter.updateProducts(products)
            }else{
                Toast.makeText(requireContext(), "Veri bulunamadı", Toast.LENGTH_SHORT).show()
            }
        }

        productViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToDetailsFragment(product: ProductsItem) {
        val bundle = Bundle().apply {
            putParcelable("product", product)
        }
        findNavController().navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
    }
}

