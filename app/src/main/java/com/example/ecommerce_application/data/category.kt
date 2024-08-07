package com.example.ecommerce_application.data


sealed class Category(val category: String) {

    object Tshirt : Category("Tshirt")
    object Jeans : Category("Jeans")
    object Jacket : Category("Jacket")
    object Activewear : Category("Activewear")
    object Shorts : Category("Shorts")
    object Sweaters : Category("Sweaters")
    object Hoodies : Category("Hoodies")
    object Halfshirts : Category("Halfshirts")
    object Printedshirts : Category("Printedshirts")
    object Pants : Category("Pants")
    object Formalshirts : Category("Formalshirts")
    object Suit : Category("Suit")

}