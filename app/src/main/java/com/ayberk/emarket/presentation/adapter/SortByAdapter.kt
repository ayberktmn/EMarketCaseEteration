package com.ayberk.emarket.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ayberk.emarket.R
import com.ayberk.emarket.databinding.SortByItemBinding
import com.ayberk.emarket.util.SortBy

class SortByAdapter : ListAdapter<SortBy, SortByAdapter.ViewHolder>(SortByDiffCallback()) {

    var selectedPosition = -1
    private var selectedSortBy: SortBy? = null

    inner class ViewHolder(private val binding: SortByItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sortBy: SortBy) {
            val context = binding.root.context
            binding.sortByText.text = sortBy.name
            binding.sortByRadioButton.isChecked = adapterPosition == selectedPosition
            binding.sortByRadioButton.isClickable = false
            binding.sortByRadioButton.buttonTintList = ContextCompat.getColorStateList(context, R.color.toolbar_blue)  // Set blue color
            binding.root.setOnClickListener {
                selectedPosition = adapterPosition
                selectedSortBy = sortBy
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SortByItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SortByDiffCallback : DiffUtil.ItemCallback<SortBy>() {
        override fun areItemsTheSame(oldItem: SortBy, newItem: SortBy): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SortBy, newItem: SortBy): Boolean {
            return oldItem == newItem
        }
    }
}