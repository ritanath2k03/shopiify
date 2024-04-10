package com.ritanath.shopiify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseUtils: FirebaseUtils

) : ViewModel() {
    private val _cartProducts =
        MutableStateFlow<Resource<List<CartSelectedProduct>>>(Resource.Unspecified())
        val cartProducts = _cartProducts.asStateFlow()
    private var cartProductDocuments = emptyList<DocumentSnapshot>()
    init {
        getProductDetalis()
    }
        private fun getProductDetalis(){
            viewModelScope.launch {
                _cartProducts.emit(Resource.Loading())
            }
            firestore.collection(Models.USERS).document(auth.uid!!).collection(Models.CART)
                .addSnapshotListener{value,e->
                    if(e!=null||value==null){
                        viewModelScope.launch { _cartProducts.emit(Resource.Error(e?.message.toString())) }
                    }
                    else{
                        val selectedCartProduct=value.toObjects(CartSelectedProduct::class.java)
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


        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when (quantityChanging) {
                FirebaseUtils.QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseUtils.QuantityChanging.DECREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
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
}