package com.example.ecommerce_application.fragments.loginRegister

//import com.example.ecommerce_application.MainActivity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.ecommerce_application.R
import com.example.ecommerce_application.adapters.ViewPagerAdapter
import com.example.ecommerce_application.databinding.FragmentNavigationBinding

class NavigationFragment : Fragment(R.layout.fragment_navigation) {
    private lateinit var binding: FragmentNavigationBinding
    private lateinit var slideViewPager: ViewPager
    private lateinit var dotIndicator: LinearLayout
    private lateinit var backButton: Button
    private lateinit var nextButton: Button
    private lateinit var skipButton: Button
    private lateinit var dots: Array<TextView?>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private val viewPagerListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            setDotIndicator(position)
            backButton.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
            nextButton.text = if (position == 2) "Finish" else "Next"
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNavigationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton = view.findViewById(R.id.backButton)
        nextButton = view.findViewById(R.id.nextButton)
        skipButton = view.findViewById(R.id.skipButton)

        backButton.setOnClickListener {
            if (getItem(0) > 0) {
                slideViewPager.setCurrentItem(getItem(-1), true)
            }
        }

        nextButton.setOnClickListener {
            if (getItem(0) < 2) {
                slideViewPager.setCurrentItem(getItem(1), true)
            } else {


//                startActivity(Intent(requireContext(), MainActivity::class.java))
//                requireActivity().finish()
                findNavController().navigate(R.id.action_navigationFragment_to_introductionFragment)

            }
        }

        skipButton.setOnClickListener {
//            startActivity(Intent(requireContext(), MainActivity::class.java))
//            requireActivity().finish()

            //  binding.buttonLoginAccountOptions.setOnClickListener {
            findNavController().navigate(R.id.action_navigationFragment_to_accountOptionsFragment)
        }


        slideViewPager = view.findViewById(R.id.slideViewPager)
        dotIndicator = view.findViewById(R.id.dotIndicator)

        viewPagerAdapter = ViewPagerAdapter(requireContext())
        slideViewPager.adapter = viewPagerAdapter

        setDotIndicator(0)
        slideViewPager.addOnPageChangeListener(viewPagerListener)
    }

    private fun setDotIndicator(position: Int) {
        dots = arrayOfNulls(3)

        dotIndicator.removeAllViews()

        for (i in dots.indices) {
            dots[i] = TextView(requireContext())
            dots[i]?.text = Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_LEGACY)
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(resources.getColor(R.color.grey, requireContext().theme))
            dotIndicator.addView(dots[i])
        }
        dots[position]?.setTextColor(resources.getColor(R.color.lavender, requireContext().theme))
    }

    private fun getItem(i: Int): Int {
        return slideViewPager.currentItem + i
    }
}
