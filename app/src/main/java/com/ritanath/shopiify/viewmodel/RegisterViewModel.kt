package com.ritanath.shopiify.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ritanath.shopiify.data.User
import com.ritanath.shopiify.util.Constants.USER_COLLECTION
import com.ritanath.shopiify.util.RegisterFieldState
import com.ritanath.shopiify.util.RegisterValidation
import com.ritanath.shopiify.util.Resource
import com.ritanath.shopiify.util.validateEmail
import com.ritanath.shopiify.util.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: FirebaseAuth,private val db:FirebaseFirestore
) : ViewModel(){
private val _register=MutableStateFlow<Resource<User>>(Resource.Unspecified())
val register: Flow<Resource<User>> =_register
    private val _validation=Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()


    fun createUserWithEmailAndPassword(user:User,password:String){
    if(checkValidation(user, password)){
        runBlocking {
            _register.emit(Resource.Loading())
        }
        auth.createUserWithEmailAndPassword(user.email,password)
            .addOnSuccessListener {
                it.user?.let {
                   saveUserInformation(it.uid,user)
                }
            }
            .addOnFailureListener{
                _register.value=Resource.Error(it.message.toString())
            }
    }
else{
    val registerFieldState=RegisterFieldState(validateEmail(user.email), validatePassword(password))
    runBlocking {
        _validation.send(registerFieldState)
    }
}

}

    private fun saveUserInformation(uid: String, user: User) {

        db.collection(USER_COLLECTION)
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String) :Boolean{
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
        return shouldRegister
    }
}