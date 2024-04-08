package com.ritanath.shopiify.fragment.catagorietype

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.ritanath.shopiify.data.Category
import com.ritanath.shopiify.util.Resource
import com.ritanath.shopiify.viewmodel.CategoryViewModel
import com.ritanath.shopiify.viewmodel.MyViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class BooksCatagoryFragment:MainBaseFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore
    val viewModel by viewModels<CategoryViewModel> { MyViewModelFactory(firestore, Category.book) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.topProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showOfferLoading()
                    }

                    is Resource.Success -> {
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        hideOfferLoading()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showBestProductsLoading()
                    }

                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        hideBestProductsLoading()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        hideBestProductsLoading()
                    }

                    else -> Unit
                }
            }
        }

    }
}