package com.ritanath.shopiify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ritanath.shopiify.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth:FirebaseAuth

) : ViewModel() {
    private val _loginState=MutableSharedFlow<Resource<FirebaseUser>>()
    val loginstate=_loginState.asSharedFlow()

    fun login(email:String,password:String){
        viewModelScope.launch { _loginState.emit(Resource.Loading()) }
        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                viewModelScope.launch { it.user?.let{_loginState.emit(Resource.Success(it))} }
            }
            .addOnFailureListener { viewModelScope.launch { _loginState.emit(Resource.Error(it.message.toString())) } }
    }
}