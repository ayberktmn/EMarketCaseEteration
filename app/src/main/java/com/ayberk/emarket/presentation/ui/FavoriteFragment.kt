package com.ayberk.emarket.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayberk.emarket.R
import com.ayberk.emarket.databinding.FragmentFavoriteBinding
import com.ayberk.emarket.presentation.adapter.FavoriteAdapter
import com.ayberk.emarket.presentation.viewmodel.FavoriteViewModel
import com.google.android.material.snackbar.Snackbar

class FavoriteFragment : Fragment() {

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var binding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        favoriteViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        favoriteAdapter = FavoriteAdapter { favoriteItem ->
            favoriteViewModel.removeFavoriteItem(favoriteItem)
            com.ayberk.emarket.util.Snackbar.showErrorSnackbar(view = requireView(), message = "Favorilerden kaldırıldı!", duration = Snackbar.LENGTH_SHORT)
        }

        binding.recyclerViewFavorites.apply {
            binding.recyclerViewFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.recyclerViewFavorites.adapter = adapter
            adapter = favoriteAdapter
        }

        favoriteViewModel.favoriteItemsLiveData.observe(viewLifecycleOwner, Observer { favoriteItems ->
            favoriteItems?.let {
                favoriteAdapter.updateFavorites(it)
            }
        })

        setupToolbar()

        return binding.root
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.headers.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.headers.toolbar.title = "Favorilerim"
        binding.headers.backButton.visibility = View.GONE
        binding.headers.toolbarTitle.visibility = View.GONE
    }
}