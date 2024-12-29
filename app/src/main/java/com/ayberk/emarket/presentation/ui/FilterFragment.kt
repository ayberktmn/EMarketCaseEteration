package com.ayberk.emarket.presentation.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.fonts.Font
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayberk.emarket.R
import com.ayberk.emarket.data.model.ProductsItem
import com.ayberk.emarket.data.room.AppDatabase
import com.ayberk.emarket.data.room.FavoriteDao
import com.ayberk.emarket.databinding.FragmentFilterBinding
import com.ayberk.emarket.databinding.FragmentHomeBinding
import com.ayberk.emarket.presentation.adapter.HomeProductsAdapter
import com.ayberk.emarket.presentation.adapter.SortByAdapter
import com.ayberk.emarket.presentation.viewmodel.FilterViewModel
import com.ayberk.emarket.presentation.viewmodel.HomeViewModel
import com.ayberk.emarket.util.SortBy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Locale

class FilterFragment : BottomSheetDialogFragment(){

    private lateinit var filterViewModel: FilterViewModel
    private lateinit var productViewModel: HomeViewModel
    private lateinit var binding: FragmentFilterBinding
    private lateinit var sortByadapter: SortByAdapter
    private lateinit var favoriteDao: FavoriteDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFilterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sortByadapter = SortByAdapter()

        productViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        filterViewModel = ViewModelProvider(this).get(FilterViewModel::class.java)

        val appDatabase = AppDatabase.getInstance(requireContext())
        favoriteDao = appDatabase.favoriteDao()

        binding.sortByRcylerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.sortByRcylerView.adapter = sortByadapter
        sortByadapter.submitList(SortBy.entries)

        filterViewModel.loadBrands()
        filterViewModel.loadModels()

        setupObservers()

        setToolbar()
        setupSearchViews()
        setupSearchViewsModel()


        binding.btnFilter.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                val layoutParams = it.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.layoutParams = layoutParams

                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }


    private fun setupObservers(){
        filterViewModel.brands.observe(viewLifecycleOwner, Observer { brands ->
            populateCheckboxes(binding, brands)
        })

        filterViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        filterViewModel.models.observe(viewLifecycleOwner, Observer { models ->
            modelsCheckboxes(binding,models)
        })
    }

    private fun setupSearchViews() {
        binding.searchBrandComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                charSequence?.let { query ->
                    filterViewModel.brands.value?.let { brands ->
                        populateCheckboxes(binding, brands, query.toString())
                    }
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun setupSearchViewsModel() {
        binding.searchModelComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                charSequence?.let { query ->
                    filterViewModel.models.value?.let { models ->
                        modelsCheckboxes(binding, models, query.toString())
                    }
                }
            }
            override fun afterTextChanged(editable: Editable?) {}
        })
    }


    private fun populateCheckboxes(binding: FragmentFilterBinding, brands: List<String>, query: String? = "") {
        val checkBoxContainer = binding.checkboxBrandContainer
        checkBoxContainer.removeAllViews()

        val filteredBrands = if (query.isNullOrEmpty()) {
            brands
        } else {
            brands.filter { it.contains(query, ignoreCase = true) }
        }

        if (filteredBrands.isEmpty()) {
            val noResultsText = TextView(context).apply {
                text = "No brands found"
                setTextColor(Color.GRAY)
                gravity = Gravity.CENTER
            }
            checkBoxContainer.addView(noResultsText)
        } else {
            filteredBrands.forEach { brand ->
                val checkBox = CheckBox(context)
                checkBox.text = brand
                checkBox.tag = brand

                checkBox.buttonTintList = resources.getColorStateList(R.color.toolbar_blue, null)

                checkBox.setOnCheckedChangeListener { _, isChecked ->

                }

                checkBoxContainer.addView(checkBox)
            }
        }
    }


    private fun modelsCheckboxes(binding: FragmentFilterBinding, models: List<String>, query: String? = "") {
        val checkBoxContainer = binding.checkboxModelContainer
        checkBoxContainer.removeAllViews()

        val montserratFont = ResourcesCompat.getFont(requireContext(), R.font.montserrat)

        val filteredModels = if (query.isNullOrEmpty()) {
            models
        } else {
            models.filter { it.contains(query, ignoreCase = true) }
        }

        if (filteredModels.isEmpty()) {
            val noResultsText = TextView(context).apply {
                text = "No models found"
                setTextColor(Color.GRAY)
                gravity = Gravity.CENTER
            }
            checkBoxContainer.addView(noResultsText)
        } else {
            filteredModels.forEach { model ->
                val checkBox = CheckBox(context)
                checkBox.text = model
                checkBox.tag = model
                checkBox.typeface = montserratFont

                checkBox.buttonTintList = resources.getColorStateList(R.color.toolbar_blue, null)

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                }

                checkBoxContainer.addView(checkBox)
            }
        }
    }

    fun setToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.headers.toolbar)

        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.headers.toolbarTitle.text = "Filter"
        binding.headers.toolbarTitle.setTextColor(Color.BLACK)

        binding.headers.toolbar.setBackgroundColor(Color.WHITE)

        binding.headers.backButton.setImageResource(R.drawable.close)

        binding.headers.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}


