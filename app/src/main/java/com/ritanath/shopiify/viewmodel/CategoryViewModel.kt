package com.ritanath.shopiify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.ritanath.shopiify.data.Category
import com.ritanath.shopiify.data.Models
import com.ritanath.shopiify.data.Product
import com.ritanath.shopiify.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val firestore:FirebaseFirestore,private val category:Category):ViewModel() {
    private val _topProducts=MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val topProducts=_topProducts.asStateFlow()

    private val _bestProducts=MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct=_bestProducts.asStateFlow()
    init {
        fetchOfferProduct()
        fetchBestProduct()
    }
    fun fetchOfferProduct(){
        viewModelScope.launch {
            _topProducts.emit(Resource.Loading())
        }
        firestore.collection(Models.PRODUCTS).whereEqualTo(Models.CATEGORY,category.category)
            .whereNotEqualTo(Models.OFFER_PERCENTAGE,null).get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    val product=it.toObjects(Product::class.java)
                    _topProducts.emit(Resource.Success(product))
                }
            }
            .addOnFailureListener {
              viewModelScope.launch{
                  _topProducts.emit(Resource.Error(it.message.toString()))
              }
            }
    }
    fun fetchBestProduct(){
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        firestore.collection(Models.PRODUCTS).whereEqualTo(Models.CATEGORY,category.category)
            .whereEqualTo(Models.OFFER_PERCENTAGE,null).get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    val product=it.toObjects(Product::class.java)
                    _bestProducts.emit(Resource.Success(product))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch{
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}