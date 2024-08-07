package com.example.ecommerce_application.fragments.shopping

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ecommerce_application.R
import com.example.ecommerce_application.adapters.HomeViewpagerAdapter
import com.example.ecommerce_application.databinding.FragmentHomeBinding
import com.example.ecommerce_application.fragments.categories.MainCategoryFragment

import com.example.ecommerce_application.fragments.categories.ActivewearFragment
import com.example.ecommerce_application.fragments.categories.FormalshirtsFragment
import com.example.ecommerce_application.fragments.categories.HalfshirtsFragment
import com.example.ecommerce_application.fragments.categories.TshirtFragment
import com.example.ecommerce_application.fragments.categories.JacketFragment
import com.example.ecommerce_application.fragments.categories.JeansFragment
import com.example.ecommerce_application.fragments.categories.PrintedshirtsFragment
import com.example.ecommerce_application.fragments.categories.ShortsFragment
import com.example.ecommerce_application.fragments.categories.SweatersFragment
import com.example.ecommerce_application.fragments.categories.HoodiesFragment
import com.example.ecommerce_application.fragments.categories.PantsFragment
import com.example.ecommerce_application.fragments.categories.SuitFragment
import com.example.ecommerce_application.util.Resource
import com.example.ecommerce_application.viewmodel.ProfileViewModel

import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    val viewModel by viewModels<ProfileViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf(
            MainCategoryFragment(),
            TshirtFragment(),
            JeansFragment(),
            JacketFragment(),
            ActivewearFragment(),
            ShortsFragment(),
            SweatersFragment(),
            HoodiesFragment(),
            HalfshirtsFragment(),
            PrintedshirtsFragment(),
            PantsFragment(),
            FormalshirtsFragment(),
            SuitFragment()
        )
        binding.ProfileImage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_userAccountFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.tvLoading.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.tvLoading.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath).error(
                            ColorDrawable(Color.BLACK)
                        ).into(binding.ProfileImage)
                        binding.tvCustomerName.text = "${it.data?.firstName}"
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.tvLoading.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }


        binding.viewpagerHome.isUserInputEnabled = false

        val viewPager2Adapter =
            HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "T-shirt"
                2 -> tab.text = "Jeans"
                3 -> tab.text = "Jackets"
                4 -> tab.text = "Active-wear"
                5 -> tab.text = "Shorts"
                6 -> tab.text = "Sweaters"
                7 -> tab.text = "Hoodies"
                8 -> tab.text = "Half-Shirt"
                9 -> tab.text = "Printed-Shirt"
                10 -> tab.text = "Pants"
                11 -> tab.text = "Formal-Shirts"
                12 -> tab.text = "Suit"
            }
        }.attach()
    }
}