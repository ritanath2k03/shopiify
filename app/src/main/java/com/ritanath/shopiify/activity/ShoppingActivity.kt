package com.ritanath.shopiify.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ritanath.shopiify.R


class ShoppingActivity : AppCompatActivity() {

    val binding by lazy {
        com.ritanath.shopiify.databinding.ActivityShoppingBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNav.setupWithNavController(navController)

    }
}