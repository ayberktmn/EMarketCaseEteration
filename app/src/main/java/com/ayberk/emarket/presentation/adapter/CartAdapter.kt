package com.ayberk.emarket.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayberk.emarket.databinding.ItemCartBinding
import com.ayberk.emarket.data.model.CartItem

class CartAdapter(private val onQuantityChange: (CartItem, Boolean) -> Unit,
                  private val onDeleteItem: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartItems: List<CartItem> = emptyList()

    class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem, onQuantityChange: (CartItem, Boolean) -> Unit, onDeleteItem: (CartItem) -> Unit) {
            binding.cartItemName.text = cartItem.name
            binding.cartItemPrice.text = cartItem.price + " ₺"
            binding.cartItemQuantity.text = cartItem.quantity.toString()

            binding.decreaseQuantity.setOnClickListener {
                onQuantityChange(cartItem, false)
            }

            binding.increaseQuantity.setOnClickListener {
                onQuantityChange(cartItem, true)
            }

            binding.imgDelete.setOnClickListener {
                onDeleteItem(cartItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem, onQuantityChange,onDeleteItem)
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }
}