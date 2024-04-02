package com.ritanath.shopiify.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.ritanath.shopiify.R
import com.ritanath.shopiify.activity.AddProducts
import com.ritanath.shopiify.activity.ShoppingActivity
import com.ritanath.shopiify.adapter.HomeViewPagerAdapter
import com.ritanath.shopiify.databinding.FragmentHomeBinding
import com.ritanath.shopiify.fragment.catagorietype.BooksCatagoryFragment
import com.ritanath.shopiify.fragment.catagorietype.ElectronicCatagoryFragment
import com.ritanath.shopiify.fragment.catagorietype.GroceryCatagoruFragment
import com.ritanath.shopiify.fragment.catagorietype.HomeCatagoryFragment
import com.ritanath.shopiify.fragment.catagorietype.ToyCatagoryFragment

class HomeFragment :Fragment(R.layout.fragment_home){
    private lateinit var  binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listOfCatagory= arrayListOf<Fragment>(HomeCatagoryFragment(),ToyCatagoryFragment(),ElectronicCatagoryFragment(),BooksCatagoryFragment(),
            GroceryCatagoruFragment()
        )

        val homeAdapter= HomeViewPagerAdapter(listOfCatagory,childFragmentManager,lifecycle)
        binding.homeviewpager.adapter=homeAdapter
        TabLayoutMediator(binding.hometabLayout,binding.homeviewpager){
            tab,position->
            when (position) {
                0 -> tab.text = "Home"
                1 -> tab.text = "Toy"
                2 -> tab.text = "Electronics"
                3 -> tab.text = "Books"
                4-> tab.text = "Grocery"
            }
        }.attach()
       binding.apply {
           binding.uploadProduct.setOnClickListener {
               Intent(requireActivity(), AddProducts::class.java).also {
                       intent ->
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                   startActivity(intent)
               }
           }
       }
    }



}