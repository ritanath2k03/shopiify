package com.ritanath.shopiify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productsadder.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.ritanath.shopiify.data.Models
import com.ritanath.shopiify.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val pagingInformation = PagingInfo()
    private val _bestDealProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealProducts: StateFlow<Resource<List<Product>>> = _bestDealProducts
    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts
    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    init {
        fetchSpecialProducts()
        fetchBestProduct()
        fetchBestDealProduct()
    }

    fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        firestore.collection(Models.PRODUCTS)
            .whereEqualTo(Models.CATEGORY, Models.SPECIAL_PRODUCTS)
            .limit(pagingInformation.specialProductPageNumber * 10).get()
            .addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }
                pagingInformation.specialProductPageNumber++
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestDealProduct() {
        viewModelScope.launch {
            _bestDealProducts.emit(Resource.Loading())
        }
        firestore.collection(Models.PRODUCTS)
            .whereEqualTo(Models.CATEGORY, Models.BEST_DEAL_PRODUCTS)
            .limit(pagingInformation.bestDealPageNumber * 10).get().addOnSuccessListener { result ->
                val bestDealProduct = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealProducts.emit(Resource.Success(bestDealProduct))
                }
                pagingInformation.bestDealPageNumber++
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProduct() {
        if (!pagingInformation.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection(Models.PRODUCTS)
                .limit(pagingInformation.bestProductPageNumber * 10).get()
                .addOnSuccessListener { result ->
                    val bestProduct = result.toObjects(Product::class.java)
                    pagingInformation.isPagingEnd =
                        bestProducts == pagingInformation.oldBestProducts
                    pagingInformation.oldBestProducts = bestProduct
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProduct))
                    }
                    pagingInformation.bestProductPageNumber++
                }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
        }
    }

}

internal data class PagingInfo(
    var bestProductPageNumber: Long = 1,
    var bestDealPageNumber: Long = 1,
    var specialProductPageNumber: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)