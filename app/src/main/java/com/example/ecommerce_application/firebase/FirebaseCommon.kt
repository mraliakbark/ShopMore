package com.example.ecommerce_application.firebase

import com.example.ecommerce_application.data.CartProduct
import com.example.ecommerce_application.data.WishlistProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.lang.Exception

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val cartCollection =
        firestore.collection("user").document(auth.uid!!).collection("cart")

    private val wishlistCollection =
        firestore.collection("user").document(auth.uid!!).collection("wishlist")

    //fun deleteWishlistProduct

    fun addProductToCart(cartProduct: CartProduct,onResult: (CartProduct?,Exception?)-> Unit){
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct,null)
            }.addOnFailureListener {
                onResult(null,it)
        }
    }
    fun addProductToWishlist(wishlistProduct: WishlistProduct,onResult: (WishlistProduct?, Exception?) -> Unit){
        wishlistCollection.document().set(wishlistProduct)
            .addOnSuccessListener {
                onResult(wishlistProduct,null)
            }.addOnFailureListener {
                onResult(null,it)
            }
    }
    fun increaseQuantity(documentId: String, OnResult: (String?, Exception?) -> Unit){
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef,newProductObject)
            }
        }.addOnSuccessListener {
            OnResult(documentId,null)
        }.addOnFailureListener {
            OnResult(null,it)

        }
    }
    fun decreaseQuantity(documentId: String, OnResult: (String?, Exception?) -> Unit){
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef,newProductObject)
            }
        }.addOnSuccessListener {
            OnResult(documentId,null)
        }.addOnFailureListener {
            OnResult(null,it)

        }
    }

    enum class QuantityChanging {
        INCREASE,DECREASE
    }
}