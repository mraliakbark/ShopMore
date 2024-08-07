package com.example.ecommerce_application.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce_application.data.Product
import com.example.ecommerce_application.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SearchProductViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _allProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val allProducts: StateFlow<Resource<List<Product>>> = _allProducts

    init {
        fetchAllProducts()
    }

     fun fetchAllProducts() {
        _allProducts.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val result = firestore.collection("Products").get().await()
                val allProducts = result.toObjects(Product::class.java)
                _allProducts.value = Resource.Success(allProducts)
            } catch (e: Exception) {
                _allProducts.value = Resource.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun searchProductsByName(query: String) {
        val currentProducts = _allProducts.value.data ?: return
        val filteredProducts = currentProducts.filter { it.name.contains(query, ignoreCase = true) }
        _allProducts.value = Resource.Success(filteredProducts)
    }
}
