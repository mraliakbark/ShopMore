package com.example.ecommerce_application.viewmodel

//import com.example.ecommerce_application.util.Event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce_application.data.CartProduct
import com.example.ecommerce_application.data.WishlistProduct
import com.example.ecommerce_application.firebase.FirebaseCommon
import com.example.ecommerce_application.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {


    interface ToastDisplay {
        fun showToast(message: String)
    }

    private var toastDisplay: ToastDisplay? = null

    fun setToastDisplay(toastDisplay: ToastDisplay) {
        this.toastDisplay = toastDisplay
    }

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    private val _addToWishlist = MutableStateFlow<Resource<WishlistProduct>>(Resource.Unspecified())
    val addToWishlist = _addToWishlist.asStateFlow()

    private val _wishlistProducts =
        MutableStateFlow<Resource<List<WishlistProduct>>>(Resource.Unspecified())
    val wishlistProducts = _wishlistProducts.asStateFlow()

    private var wishlistProductDocuments = emptyList<DocumentSnapshot>()


//    private val _toastMessage = MutableLiveData<Event<String>>()
//    val toastMessage: LiveData<Event<String>> = _toastMessage


    fun addUpdateProductInCart(cartProduct: CartProduct) {
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()) { //Add new Product
                        addNewProduct(cartProduct)
                    } else {
                        val product = it.first().toObject(CartProduct::class.java)
                        if (product == cartProduct) { // Increase the quantity
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else {// Add new product
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }
    }

    fun addProductInWishlist(wishlistProduct: WishlistProduct) {
        viewModelScope.launch { _addToWishlist.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("wishlist")
            .whereEqualTo("product.id", wishlistProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()) { //Add new Product
                        addToWishlist(wishlistProduct)
                    } else {
                        val product = it.first().toObject(WishlistProduct::class.java)
                        if (product == wishlistProduct) { // Increase the quantity
//                            _toastMessage.postValue(Event("Product is already in the wishlist"))
//                            toastDisplay?.showToast("Product is already in the wishlist")
                            deleteWishlistProduct(wishlistProduct)


                        } else {// Add new product
                            addToWishlist(wishlistProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToWishlist.emit(Resource.Error(it.message.toString())) }
            }
    }
    fun deleteWishlistProduct(wishlistProduct: WishlistProduct) {
        val index = wishlistProducts.value.data?.indexOf(wishlistProduct)
        if (index != null && index != -1) {
            val documentId = wishlistProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("wishlist")
                .document(documentId).delete()
            //Toast.makeText(context, "Product removed from wishlist", Toast.LENGTH_SHORT).show()

        }
    }


    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addedProduct, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(addedProduct!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun addToWishlist(wishlistProduct: WishlistProduct) {
        firebaseCommon.addProductToWishlist(wishlistProduct) { addedToWishlist, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToWishlist.emit(Resource.Success(addedToWishlist!!))
                else
                    _addToWishlist.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) { _, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(cartProduct))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }

        }
    }
}