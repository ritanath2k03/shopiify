package com.ritanath.shopiify.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ritanath.shopiify.R
import com.ritanath.shopiify.data.CartSelectedProduct
import com.ritanath.shopiify.util.Resource
import com.ritanath.shopiify.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    val binding by lazy {
        com.ritanath.shopiify.databinding.ActivityShoppingBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<CartViewModel> ()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNav.setupWithNavController(navController)

        cartUpdation()
    }

    private fun cartUpdation() {

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        val count = it.data?.size ?: 0
                        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNav)
                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.g_light_red)
                            badgeTextColor=resources.getColor(R.color.dark_gray)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}