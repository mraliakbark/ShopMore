package com.example.ecommerce_application.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_application.R
import com.example.ecommerce_application.adapters.SearchProductAdapter
import com.example.ecommerce_application.databinding.FragmentSearchProductsBinding
import com.example.ecommerce_application.util.Resource
import com.example.ecommerce_application.viewmodel.SearchProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class SearchProductFragment : Fragment() {
    private lateinit var binding: FragmentSearchProductsBinding
    private val viewModel: SearchProductViewModel by viewModels()
    private val searchProductAdapter: SearchProductAdapter by lazy { SearchProductAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        observeProducts()


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search on submit if needed
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    // Fetch all products again when the search query is empty
                    fetchAllProducts()
                } else {
                    newText?.let { searchProducts(it) }
                }
                return true
            }
        })
        binding.searchView.setOnCloseListener {
            fetchAllProducts()
            false
        }
        binding.searchView.requestFocus()

    }

    private fun setupRecyclerView() {
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchProductAdapter
        }

        searchProductAdapter.onClick = { product ->
            val bundle = Bundle().apply { putParcelable("product", product) }
            findNavController().navigate(
                R.id.action_searchProductFragment_to_productDetailFragment,
                bundle
            )
        }
    }

    private fun observeProducts() {
        lifecycleScope.launchWhenStarted {
            viewModel.allProducts.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.SearchProductsProgressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        val allProducts = resource.data
                        searchProductAdapter.differ.submitList(resource.data)
                        binding.SearchProductsProgressBar.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
                            .show()
                        binding.SearchProductsProgressBar.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun searchProducts(query: String) {
        viewModel.searchProductsByName(query)
    }
    private fun fetchAllProducts(){
        viewModel.fetchAllProducts()
    }

}
