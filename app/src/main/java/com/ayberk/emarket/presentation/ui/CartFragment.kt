package com.ayberk.emarket.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayberk.emarket.R
import com.ayberk.emarket.data.model.CartItem
import com.ayberk.emarket.databinding.FragmentCartBinding
import com.ayberk.emarket.presentation.adapter.CartAdapter
import com.ayberk.emarket.presentation.viewmodel.CartViewModel
import com.google.android.material.snackbar.Snackbar
import java.math.BigDecimal

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        setupToolbar()

        cartAdapter = CartAdapter(
            onQuantityChange = { cartItem, increase ->
                if (increase) cartViewModel.increaseQuantity(cartItem) else cartViewModel.decreaseQuantity(cartItem)
            },
            onDeleteItem = { cartItem ->
                showDeleteConfirmationDialog(cartItem)
            }
        )

        binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCart.adapter = cartAdapter

        cartViewModel.favoriteCartsLiveData.observe(viewLifecycleOwner, Observer { cartItems ->
            if (cartItems.isNullOrEmpty()) {
                cartAdapter.updateCartItems(emptyList())
                Toast.makeText(requireContext(), "Sepetiniz boş!", Toast.LENGTH_SHORT).show()
            } else {
                cartAdapter.updateCartItems(cartItems)
            }
        })

        cartViewModel.totalPriceLiveData.observe(viewLifecycleOwner, Observer { totalPrice ->
            binding.txtTotalPrice.text = String.format("%.2f ₺", totalPrice)
        })

        binding.btnComplete.setOnClickListener {
            com.ayberk.emarket.util.Snackbar.showAlreadyJoinedSnackbar(view = requireView(), message = "Siparişiniz Alındı", duration = Snackbar.LENGTH_SHORT)
        }

        cartViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        return binding.root
    }

    private fun showDeleteConfirmationDialog(cartItem: CartItem) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Sepetten Kaldır")
            .setMessage("Bu ürünü sepetinizden kaldırmak istiyor musunuz?")
            .setPositiveButton("Evet") { dialog, which ->
                cartViewModel.deleteCartItem(cartItem)
            }
            .setNegativeButton("Hayır") { dialog, which ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()

        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))

        val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.toolbar_blue))
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.headers.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.headers.toolbar.title = "E-Market"
        binding.headers.backButton.visibility = View.GONE
        binding.headers.toolbarTitle.visibility = View.GONE
    }
}
