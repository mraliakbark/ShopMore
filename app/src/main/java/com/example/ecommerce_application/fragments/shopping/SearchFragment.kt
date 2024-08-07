package com.example.ecommerce_application.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ecommerce_application.R
import com.example.ecommerce_application.adapters.SearchProductAdapter
import com.example.ecommerce_application.databinding.FragmentSearchBinding
import com.example.ecommerce_application.viewmodel.SearchProductViewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the LottieAnimationView by its ID
        val lottieAnimationView = binding.lottieAnimationView

        // Set animation from a raw JSON file
        lottieAnimationView.setAnimation(R.raw.search_animation)

        // Set loop to true (if not already set in XML)
        lottieAnimationView.loop(true)

        // Start the animation
        lottieAnimationView.playAnimation()

        binding.searchBar.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchProductFragment)


        }
    }
}