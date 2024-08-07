package com.example.ecommerce_application.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce_application.R
import com.example.ecommerce_application.adapters.ColorsAdapter
import com.example.ecommerce_application.adapters.SizesAdapter
import com.example.ecommerce_application.adapters.ViewPager2Images
import com.example.ecommerce_application.data.CartProduct
import com.example.ecommerce_application.data.WishlistProduct
import com.example.ecommerce_application.databinding.FragmentProductDetailsBinding
import com.example.ecommerce_application.helper.getProductPrice
import com.example.ecommerce_application.util.Resource
import com.example.ecommerce_application.util.hideBottomNavigationView
import com.example.ecommerce_application.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {
    private val args by navArgs<ProductDetailFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsViewModel>()
    var isFavrite = false;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorRV()
        setupViewpager()

        binding.ImageGoBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.ImageWishlist.setOnClickListener {
            toggleFavorite();
        }

        sizesAdapter.onItemClick = {
            selectedSize = it
        }

        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        binding.buttonAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize))
        }


        viewModel.setToastDisplay(object : DetailsViewModel.ToastDisplay {
            override fun showToast(message: String) {
                showToast(message)
            }
        })
//        viewModel.toastMessage.observe(viewLifecycleOwner) { event ->
//            event.getContentIfNotHandled()?.let { message ->
//                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//            }
//        }
//        viewModel.toastMessage.observe(viewLifecycleOwner, { event: Lifecycle.Event<String> ->
//            event.getContentIfNotHandled()?.let { message ->
//                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//            }
//        })


        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonAddToCart.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonAddToCart.revertAnimation()
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                    }

                    is Resource.Error -> {
                        binding.buttonAddToCart.stopAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }

        binding.apply {
            val priceAfterOffer =
                product.offerPercentage?.getProductPrice(product.price) ?: product.price

            tvProductName.text = product.name
            tvProductPrice.text = "₹ ${String.format("%.2f", priceAfterOffer)}"
            tvProductDescription.text = product.description

            if (product.colors.isNullOrEmpty())
                tvProductColors.visibility = View.INVISIBLE

            if (product.sizes.isNullOrEmpty())
                tvProductSize.visibility = View.INVISIBLE
        }
        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }


        binding.ImageWishlist.setOnClickListener {
            Toast.makeText(requireContext(), "Added to Wishlist ", Toast.LENGTH_SHORT).show()
            viewModel.addProductInWishlist(WishlistProduct(product))

        }

    }

    private fun toggleFavorite() {
        isFavrite = !isFavrite;
        if (isFavrite) {
            Toast.makeText(requireContext(), "Added to favourite ", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(requireContext(), "Remove to favourite ", Toast.LENGTH_SHORT).show()


        }
        updateUi();
    }


    private fun updateUi() {
        if (isFavrite) {
            binding.ImageWishlist.setImageResource(R.drawable.ic_heart_filled)

        } else {
            binding.ImageWishlist.setImageResource(R.drawable.ic_heart)
        }
    }

    private fun setupViewpager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }

    private fun setupColorRV() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

}