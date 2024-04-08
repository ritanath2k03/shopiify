package com.ritanath.shopiify.fragment.catagorietype

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ritanath.shopiify.R
import com.ritanath.shopiify.adapter.BestDealsAdapter
import com.ritanath.shopiify.adapter.BestProductsAdapter
import com.ritanath.shopiify.adapter.SpecialProductAdapter
import com.ritanath.shopiify.databinding.FragmentHomeCatagoryBinding
import com.ritanath.shopiify.util.Resource
import com.ritanath.shopiify.viewmodel.HomeCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeCatagoryFragment:Fragment(R.layout.fragment_home_catagory) {
    private lateinit var specialProductsAdapter:SpecialProductAdapter
    private lateinit var binding:FragmentHomeCatagoryBinding
    private lateinit var bestDealAdapter: BestDealsAdapter
    private lateinit var bestProductAdapter: BestProductsAdapter
    val viewModel by viewModels<HomeCategoryViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeCatagoryBinding.inflate(inflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpecialProductsRv()
        bestDealsRv()
        bestProductRv()
        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest { res->
                when(res){
                    is Resource.Loading->showLoading()
                    is Resource.Error->{
                        hideLoading()
                        Toast.makeText(requireContext(), res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                        is Resource.Success-> {
                            specialProductsAdapter.differ.submitList(res.data)
                            hideLoading()
                        }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading->showLoading()
                    is Resource.Error->{
                        hideLoading()
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success-> {
                        bestProductAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    else -> Unit
                }
            }

        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestDealProducts.collectLatest {res->
                when(res){
                    is Resource.Loading->showLoading()
                    is Resource.Error->{
                        hideLoading()
                        Toast.makeText(requireContext(), res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success-> {
                        bestDealAdapter.differ.submitList(res.data)
                        hideLoading()
                    }
                    else -> Unit
                }
            }
        }

        specialProductsAdapter.onClick={
            val bundle=Bundle().apply {
                putParcelable("products",it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productViewFragment,bundle)
        }
        bestDealAdapter.onClick={
            val bundle=Bundle().apply {
                putParcelable("products",it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_productViewFragment,bundle)
        }

        bestProductAdapter.onClick={
            val bundle=Bundle().apply {
                putParcelable("products",it)
            }

            findNavController().navigate(R.id.action_homeFragment_to_productViewFragment,bundle)
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                viewModel.fetchBestProduct()
            }
        })
    }

    private fun bestProductRv() {
        bestProductAdapter= BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager=GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter=bestProductAdapter
        }
    }

    private fun bestDealsRv() {
        bestDealAdapter= BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealAdapter
        }
    }

    private fun hideLoading() {
       //yet to implement animation
    }
    private fun showLoading() {
      //yet to implement animation
    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }
}