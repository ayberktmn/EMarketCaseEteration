package com.ayberk.emarket

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.ayberk.emarket.databinding.ActivityMainBinding
import com.ayberk.emarket.presentation.viewmodel.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var cartViewModel: CartViewModel
    private var currentFragmentId: Int = R.id.homeFragment // Initialize with the default fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // BottomNavigationView'e NavController'ı bağla
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        cartViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CartViewModel::class.java]

        cartViewModel.observeCartItemCount()
        // Observe changes in cart item count and update the badge
        cartViewModel.cartItemCount.observe(this) { count ->
            updateBadge(count)
        }
        // BottomNavigationView ile yönlendirme
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            if (currentFragmentId == item.itemId) {
                // If the selected fragment is already the current one, do nothing
                return@setOnNavigationItemSelectedListener false
            }

            when (item.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.homeFragment)
                    currentFragmentId = R.id.homeFragment
                    true
                }

                R.id.basket -> {
                    navController.navigate(R.id.cartFragment)
                    currentFragmentId = R.id.cartFragment
                    true
                }

                R.id.favorites -> {
                    navController.navigate(R.id.favoriteFragment)
                    currentFragmentId = R.id.favoriteFragment
                    true
                }

                else -> false
            }
        }
    }

    fun updateBadge(count: Int) {
        val badge = binding.bottomNav.getOrCreateBadge(R.id.basket)
        badge.number = count
        badge.isVisible = count > 0
    }
}
