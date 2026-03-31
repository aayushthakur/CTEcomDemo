package com.clevertap.demo.ecom.mainFragments

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.clevertap.demo.ecom.R
import com.clevertap.demo.ecom.databinding.FragmentItemDetailsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ItemDetailsFragment : Fragment() {

    private lateinit var binding: FragmentItemDetailsBinding

    companion object {
        private const val ARG_PRODUCT = "product"

        fun newInstance(product: Product): ItemDetailsFragment {
            val fragment = ItemDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_PRODUCT, product)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.getParcelable<Product>(ARG_PRODUCT)

        if (product != null) {
            binding.toolbarDetails.title = product.title
            binding.productTitleDetails.text = product.title
            binding.productPriceDetails.text = "₹${product.price}"
            binding.productOriginalPriceDetails.text = "₹${product.original_price}"
            binding.productOriginalPriceDetails.paintFlags = binding.productOriginalPriceDetails.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.productDiscountDetails.text = product.discount
            Glide.with(this).load(product.image_url).into(binding.productImageDetails)
        }

        binding.toolbarDetails.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE
    }
}