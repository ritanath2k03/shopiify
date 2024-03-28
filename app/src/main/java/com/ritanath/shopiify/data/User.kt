package com.ritanath.shopiify.data

import java.util.Date

data class User(
    val name: String,
    val email:String,
    val signUpDate: String ="",
    var imagePath:String =""

) {
    constructor():this("","","","")
}