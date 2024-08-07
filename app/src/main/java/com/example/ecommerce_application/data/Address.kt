package com.example.ecommerce_application.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTitle: String,
    val fullname: String,
    val street: String,
    val phone: String,
    val city: String,
    val state: String
): Parcelable {
    constructor(): this("","","","","","")
}