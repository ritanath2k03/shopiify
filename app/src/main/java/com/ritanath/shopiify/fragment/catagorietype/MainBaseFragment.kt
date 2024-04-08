package com.ritanath.shopiify.fragment.catagorietype

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ritanath.shopiify.R
import com.ritanath.shopiify.adapter.BestProductsAdapter
import com.ritanath.shopiify.databinding.FragmentMainBaseBinding

open class MainBaseFragment : Fragment(R.layout.fragment_main_base) {
    private lateinit var binding: FragmentMainBaseBinding
    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val  bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBaseBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        bestProductsAdapter.onClick={
            val bundle=Bundle().apply {
                putParcelable("products",it)
            }

            findNavController().navigate(R.id.action_homeFragment_to_productViewFragment,bundle)
        }
        offerAdapter.onClick={
            val bundle=Bundle().apply {
                putParcelable("products",it)
            }

            findNavController().navigate(R.id.action_homeFragment_to_productViewFragment,bundle)
        }
        setupOfferRv()
        setupBestProductsRv()
        bindings()
    }

    private fun bindings() {
        binding.rvOfferProducts.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dx != 0){
                    onOfferPagingRequest()
                }
            }
        })
        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                onBestProductsPaging()
            }
        })
    }

    open fun onBestProductsPaging() {

    }

    open fun onOfferPagingRequest() {

    }

    private fun setupBestProductsRv() {
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setupOfferRv() {
        binding.rvOfferProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = offerAdapter
        }
    }

    fun showOfferLoading(){
        binding.offerProductsProgressBar.visibility = View.VISIBLE
    }

    fun hideOfferLoading(){
        binding.offerProductsProgressBar.visibility = View.GONE
    }

    fun showBestProductsLoading(){
        binding.bestProductsProgressBar.visibility = View.VISIBLE
    }

    fun hideBestProductsLoading(){
        binding.bestProductsProgressBar.visibility = View.GONE
    }


}