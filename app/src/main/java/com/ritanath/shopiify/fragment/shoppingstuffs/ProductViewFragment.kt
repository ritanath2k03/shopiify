package com.ritanath.shopiify.fragment.shoppingstuffs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ritanath.shopiify.R
import com.ritanath.shopiify.adapter.ColorsAdapter
import com.ritanath.shopiify.adapter.ImageViewAdapter
import com.ritanath.shopiify.adapter.SizeAdapter
import com.ritanath.shopiify.databinding.FragmentProductViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductViewFragment:Fragment() {
    private lateinit var binding:FragmentProductViewBinding
    private val imageAdapter by lazy { ImageViewAdapter() }
    private val colorAdapter by lazy { ColorsAdapter() }
    private val sizeAdapter by lazy { SizeAdapter() }
    private val productArgs by navArgs<ProductViewFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProductViewBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product=productArgs?.products
        setUpInfo()
        binding.apply {
            tvProductName.text = product?.name
                tvProductPrice.text = "$ ${product?.price}"
            tvProductDescription.text = product?.description

            if (product?.colors.isNullOrEmpty())
                tvProductColors.visibility = View.INVISIBLE
            if (product?.sizes.isNullOrEmpty())
                tvProductSize.visibility = View.INVISIBLE
        }


        imageAdapter.differ.submitList(product?.images)
        product?.colors?.let { colorAdapter.differ.submitList(it) }
        product?.sizes?.let { sizeAdapter.differ.submitList(it) }
    }

    private fun setUpInfo() {
        setUpColors()
        setUpImages()
        setUpSize()


    }

    private fun setUpSize() {
        binding.rvSizes.apply {
            adapter=sizeAdapter
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun setUpImages() {
        binding.apply {
viewPagerProductImages.adapter=imageAdapter
        }
    }

    private fun setUpColors() {
        binding.rvColors.apply {
            adapter=colorAdapter
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }
}