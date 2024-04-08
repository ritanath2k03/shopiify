package com.ritanath.shopiify.data

sealed class Category(val category:String) {
    object book:Category(Models.BOOK)
    object toy:Category(Models.TOY)
    object grocery:Category(Models.GROCERY)
    object electronics:Category(Models.ELECTRONICS)
}