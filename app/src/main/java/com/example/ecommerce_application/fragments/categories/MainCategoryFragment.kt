package com.example.ecommerce_application.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ecommerce_application.R
import com.example.ecommerce_application.adapters.BestDealsAdapter
import com.example.ecommerce_application.adapters.BestProductsAdapter
import com.example.ecommerce_application.adapters.SpecialProductsAdapter
import com.example.ecommerce_application.databinding.FragmentMainCategoryBinding
import com.example.ecommerce_application.util.Resource
import com.example.ecommerce_application.util.showBottomNavigationView
import com.example.ecommerce_application.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment:Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter

    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductRv()
        setupBestDealsRv()
        setupBestProductsRv()

        // Initialize the ImageSlider
        val slideModels = listOf(
            SlideModel(R.drawable.image_1, ScaleTypes.FIT),
            SlideModel(R.drawable.image_2, ScaleTypes.FIT),
            SlideModel(R.drawable.image_3, ScaleTypes.FIT)
        )
        binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT)

//        binding.imageSlider{
//
//        }

        specialProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment,b)
        }
        bestDealsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment,b)
        }
        bestProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailFragment,b)
        }


        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest{
                when (it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success ->{
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()

                    }
                    else -> Unit
                }

            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest{
                when (it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success ->{
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()

                    }
                    else -> Unit
                }

            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest{
                when (it){
                    is Resource.Loading ->{
                        binding.BestProductsProgressbar.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        bestProductsAdapter.differ.submitList(it.data)
                        binding.BestProductsProgressbar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                        binding.BestProductsProgressbar.visibility = View.GONE


                    }
                    else -> Unit
                }

            }
        }

        binding.nestedScrolledMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v,_,scrollY,_,_ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.fetchBestProducts()
            }

        })

    }


    private fun setupBestProductsRv() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter = bestProductsAdapter
        }

    }

    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            //adapter = bestProductsAdapter
            adapter = bestDealsAdapter
        }
    }

    private fun setupSpecialProductRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = specialProductsAdapter
        }
    }
    private fun hideLoading() {
        binding.MainCategoryProgressbar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.MainCategoryProgressbar.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }
}


