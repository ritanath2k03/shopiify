package com.ritanath.shopiify.util

import android.util.Patterns

fun validateEmail(email : String) :RegisterValidation{
    if(email.isEmpty())return RegisterValidation.Failed("Email Can't be empty")
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())return  RegisterValidation.Failed("Give a correct Email")
    return RegisterValidation.Success
}
fun validatePassword(password : String) :RegisterValidation{
    if(password.isEmpty())return RegisterValidation.Failed("password Can't be empty")
    if(password.length<6)return  RegisterValidation.Failed("Password should contain 6 letters")
    return RegisterValidation.Success
}