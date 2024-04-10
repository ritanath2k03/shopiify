package com.ritanath.shopiify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ritanath.shopiify.data.CartSelectedProduct
import com.ritanath.shopiify.data.Models
import com.ritanath.shopiify.firebase.FirebaseUtils
import com.ritanath.shopiify.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartDetailViewModel@Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseUtils: FirebaseUtils
) :ViewModel(){
    private val _addToCart=MutableStateFlow<Resource<CartSelectedProduct>>(Resource.Unspecified())
    val addToCart=_addToCart.asStateFlow()
    fun addUpdateProductInCart(cartProduct: CartSelectedProduct) {
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        firestore.collection(Models.USERS).document(auth.uid!!).collection(Models.CART)
            .whereEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()) { //Add new product
                        addNewProduct(cartProduct)
                    } else {
                        val productitem = it.first().toObject(CartSelectedProduct::class.java)
                        if(productitem?.product == cartProduct.product && productitem?.selectedColor == cartProduct.selectedColor && productitem.selectedSize== cartProduct.selectedSize){ //Increase the quantity (fixed quantity increasement issue)
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else { //Add new product
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartSelectedProduct) {

        firebaseUtils.increaseQuantity(documentId){ _, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(cartProduct))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }

        }
    }

    private fun addNewProduct(cartProduct: CartSelectedProduct) {
        firebaseUtils.addProductToCart(cartProduct) { addedProduct, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(addedProduct!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

}