package com.ritanath.shopiify.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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


    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    init {
        checkLoggedInState()
    }

    private fun checkLoggedInState() {
        Log.d("userStatus","working")
        val currentUser = FirebaseAuth.getInstance().currentUser
        _isLoggedIn.value = currentUser != null
    }



}