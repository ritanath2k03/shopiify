package com.ritanath.shopiify.data

data class CartSelectedProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null)
{
    constructor() : this(Product(), 1, null, null)
}
