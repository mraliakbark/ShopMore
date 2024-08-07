package com.example.ecommerce_application.fragments.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce_application.R
import com.example.ecommerce_application.adapters.WishlistAdapter
import com.example.ecommerce_application.databinding.FragmentWishlistBinding
import com.example.ecommerce_application.util.Resource
import com.example.ecommerce_application.util.VerticalItemDecoration
import com.example.ecommerce_application.viewmodel.WishlistViewModel
import kotlinx.coroutines.flow.collectLatest

class WishlistFragment : Fragment(R.layout.fragment_wishlist) {
    private lateinit var binding: FragmentWishlistBinding
    private val wishlistAdapter by lazy { WishlistAdapter() }
    private val viewModel by activityViewModels<WishlistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishlistBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()



        wishlistAdapter.onProductClick = {
            val b = Bundle().apply {
                putParcelable("product", it.product)
            }
            findNavController().navigate(R.id.action_wishlist_fragment_to_productDetailFragment, b)
        }

        wishlistAdapter.onProductDelete = { wishlistProduct ->
//            viewModel.deleteWishlistProduct(wishlistProduct)
                    val alertDialog = AlertDialog.Builder(requireContext()).apply {
                        setTitle("Delete item from Wishlist")
                        setMessage("Do you want to delete this item from your wishlist?")
                        setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        setPositiveButton("Yes") { dialog, _ ->
                            viewModel.deleteWishlistProduct(wishlistProduct)
                            dialog.dismiss()
                            hideOtherViews()

                        }
                    }
                    alertDialog.create()
                    alertDialog.show()
                }





//        cartAdapter.onPlusClick = {
//            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
//        }
//
//        cartAdapter.onMinusClick = {
//            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
//        }

//        binding.buttonCheckout.setOnClickListener {
//            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(
//                totalPrice, cartAdapter.differ.currentList.toTypedArray(),
//                true
//            )
//
//            findNavController().navigate(action)
//        }

//        binding.buttonCheckout.setOnClickListener {
//            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,cartAdapter.differ.currentList.toTypedArray(),true)
//            findNavController().navigate(action)
//        }


        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from Wishlist")
                    setMessage("Do you want to delete this item from your wishlist?")
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes") { dialog, _ ->
                        viewModel.deleteWishlistProduct(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.wishlistProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarWishlist.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarWishlist.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            showEmptyWishlist()

                        } else {
                            hideEmptyWishlist()
                            showOtherViews()
                            wishlistAdapter.differ.submitList(it.data)
                        }
                    }

                    is Resource.Error -> {
                        binding.progressbarWishlist.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvWishlist.visibility = View.VISIBLE
        }
    }
    private fun hideOtherViews() {
        binding.apply {
            rvWishlist.visibility = View.GONE
        }
    }


    private fun hideEmptyWishlist() {
        binding.apply {
            layoutWishlistEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyWishlist() {
        binding.apply {
            layoutWishlistEmpty.visibility = View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvWishlist.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = wishlistAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}