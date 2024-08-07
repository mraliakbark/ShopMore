package com.example.ecommerce_application.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WishlistProduct(
    val product: Product
) : Parcelable {
    constructor() : this(Product())
}
