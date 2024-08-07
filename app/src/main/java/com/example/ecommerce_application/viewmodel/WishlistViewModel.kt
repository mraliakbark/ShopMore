package com.example.ecommerce_application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce_application.data.WishlistProduct
import com.example.ecommerce_application.firebase.FirebaseCommon
import com.example.ecommerce_application.helper.getProductPrice
import com.example.ecommerce_application.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,

    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _wishlistProducts =
        MutableStateFlow<Resource<List<WishlistProduct>>>(Resource.Unspecified())
    val wishlistProducts = _wishlistProducts.asStateFlow()

    val productPrice = wishlistProducts.map {
        when (it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }

            else -> null
        }
    }

    private val _deleteDialog = MutableSharedFlow<WishlistProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private var wishlistProductDocuments = emptyList<DocumentSnapshot>()

    fun deleteWishlistProduct(wishlistProduct: WishlistProduct) {
        val index = wishlistProducts.value.data?.indexOf(wishlistProduct)
        if (index != null && index != -1) {
            val documentId = wishlistProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!)
                .collection("wishlist").document(documentId)
                .delete()
                .addOnSuccessListener {
                    // If deletion is successful
                    val newList = wishlistProducts.value.data?.toMutableList()
                    newList?.remove(wishlistProduct)
                    _wishlistProducts.value = Resource.Success(newList ?: emptyList())
                }
                .addOnFailureListener { exception ->
                    // If deletion fails
                    viewModelScope.launch {
                        _wishlistProducts.emit(Resource.Error(exception.message.toString()))
                    }
                }
        }
    }


    private fun calculatePrice(data: List<WishlistProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)).toDouble()
        }.toFloat()
    }


    init {
        getWishlistProducts()
    }

    private fun getWishlistProducts() {
        viewModelScope.launch { _wishlistProducts.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("wishlist")
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch { _wishlistProducts.emit(Resource.Error(error?.message.toString())) }
                } else {
                    wishlistProductDocuments = value.documents
                    val wishlistProducts = value.toObjects(WishlistProduct::class.java)
                    viewModelScope.launch { _wishlistProducts.emit(Resource.Success(wishlistProducts)) }
                }
            }
    }

}