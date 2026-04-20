package com.clevertap.demo.ecom.mainFragments

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.demo.ecom.MyApplication
import com.clevertap.demo.ecom.R
import com.clevertap.demo.ecom.databinding.FragmentItemDetailsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ItemDetailsFragment : Fragment() {

    private lateinit var binding: FragmentItemDetailsBinding
    private var cleverTapDefaultInstance: CleverTapAPI? = null

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
        cleverTapDefaultInstance = MyApplication.getInstance().clevertap()

        val product = arguments?.getParcelable<Product>(ARG_PRODUCT)

        if (product != null) {
            setupUI(product)
            
            // Track Product Viewed Event
            val prodDetails = HashMap<String, Any>()
            prodDetails["Product Name"] = product.title
            prodDetails["Product ID"] = product.id
            prodDetails["Price"] = product.price
            cleverTapDefaultInstance?.pushEvent("Product Viewed", prodDetails)

            binding.addToCartButton.setOnClickListener {
                if (CartManager.isProductInCart(product.id)) {
                    // 1. Pop backstack to root (Home)
                    parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    
                    // 2. Switch to Cart tab via Bottom Navigation
                    val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    bottomNav?.selectedItemId = R.id.cart
                } else {
                    CartManager.addToCart(product)
                    // Track Added to Cart Event
                    cleverTapDefaultInstance?.pushEvent("Added to Cart", prodDetails)
                    Toast.makeText(requireContext(), "${product.title} added to cart", Toast.LENGTH_SHORT).show()
                    updateCartButton(product)
                }
            }

            binding.buyNowButton.setOnClickListener {
                // Track Charged Event
                val chargeDetails = HashMap<String, Any>()
                chargeDetails["Amount"] = product.price
                chargeDetails["Payment Mode"] = "COD"
                
                val item = HashMap<String, Any>()
                item["Product Name"] = product.title
                item["Product ID"] = product.id
                item["Price"] = product.price
                
                val items = ArrayList<HashMap<String, Any>>()
                items.add(item)
                
                cleverTapDefaultInstance?.pushChargedEvent(chargeDetails, items)
                Toast.makeText(requireContext(), "Order Placed Successfully", Toast.LENGTH_SHORT).show()
            }
        }

        binding.toolbarDetails.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupUI(product: Product) {
        binding.toolbarDetails.title = product.title
        binding.productTitleDetails.text = product.title
        binding.productPriceDetails.text = "₹${product.price}"
        binding.productOriginalPriceDetails.text = "₹${product.original_price}"
        binding.productOriginalPriceDetails.paintFlags = binding.productOriginalPriceDetails.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.productDiscountDetails.text = product.discount
        Glide.with(this).load(product.image_url).into(binding.productImageDetails)
        updateCartButton(product)
    }

    private fun updateCartButton(product: Product) {
        if (CartManager.isProductInCart(product.id)) {
            binding.addToCartButton.text = "Go to Cart"
        } else {
            binding.addToCartButton.text = "Add to Cart"
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }
}