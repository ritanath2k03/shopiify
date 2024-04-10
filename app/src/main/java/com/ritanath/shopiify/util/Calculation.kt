package com.ritanath.shopiify.util

fun Float?.getPrice(price: Float): Float{

    if (this == null)
        return price
    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}