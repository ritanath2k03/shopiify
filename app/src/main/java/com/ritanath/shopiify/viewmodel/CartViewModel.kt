package com.ritanath.shopiify.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.ritanath.shopiify.data.CartSelectedProduct
import com.ritanath.shopiify.data.Models
import com.ritanath.shopiify.firebase.FirebaseUtils
import com.ritanath.shopiify.util.Resource
import com.ritanath.shopiify.util.getPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseUtils: FirebaseUtils

) : ViewModel() {
    private val _deleteDialog=MutableSharedFlow<CartSelectedProduct>()
    val deleteDialog=_deleteDialog.asSharedFlow()
    private val _cartProducts =
        MutableStateFlow<Resource<List<CartSelectedProduct>>>(Resource.Unspecified())
        val cartProducts = _cartProducts.asStateFlow()
    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    private fun calculatePrice(data: List<CartSelectedProduct>): Float? {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()
    }

    private var cartProductDocuments = emptyList<DocumentSnapshot>()
    init {
        getProductDetalis()
    }

    private fun getProductDetalis() {
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }
        firestore.collection(Models.USERS).document(auth.uid!!).collection(Models.CART)
            .addSnapshotListener { value, e ->
                if (e != null || value == null) {
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(e?.message.toString())) }
                } else {
                    val selectedCartProduct = value.toObjects(CartSelectedProduct::class.java)
                    cartProductDocuments = value.documents // Ensure this is updated
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Success(selectedCartProduct))
                    }
                }
            }
    }


    fun changeQuantity(
        cartProduct: CartSelectedProduct,
        quantityChanging: FirebaseUtils.QuantityChanging
    ) {

        val index = cartProducts.value.data?.indexOf(cartProduct)
        Log.d("Index",index.toString())

        if (index != null && index >= 0 && index < cartProductDocuments.size) { // Check if index is valid
            val documentId = cartProductDocuments[index].id
            when (quantityChanging) {
                FirebaseUtils.QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseUtils.QuantityChanging.DECREASE -> {
                    if(cartProduct.quantity==1){
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                    }else{
                        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                        decreaseQuantity(documentId)
                    }

                }
            }
        } else {
            Log.d("Error", "Invalid index or empty list")
        }
    }
    private fun decreaseQuantity(documentId: String) {

        firebaseUtils.decreaseQuantity(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseUtils.increaseQuantity(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }




    fun deleteCartProduct(cartProduct: CartSelectedProduct) {
        Log.d("Log", "Attempting to delete product")

        val index = cartProducts.value.data?.indexOf(cartProduct)
        Log.d("Index", index.toString())

        if (index != null && index >= 0 && index < cartProductDocuments.size) { // Ensure index is valid
            val documentId = cartProductDocuments[index].id
            Log.d("Log", "Deleting document with ID: $documentId")

            firestore.collection(Models.USERS).document(auth.uid!!).collection(Models.CART)
                .document(documentId).delete()
                .addOnSuccessListener {
                    Log.d("Log", "Product successfully deleted")
                }
                .addOnFailureListener { e ->
                    Log.e("Log", "Error deleting product: ${e.message}")
                }
        } else {
            Log.d("Log", "Invalid index or empty list")
        }
    }

}