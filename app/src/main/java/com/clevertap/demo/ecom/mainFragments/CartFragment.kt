package com.clevertap.demo.ecom.mainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.demo.ecom.MyApplication
import com.clevertap.demo.ecom.R
import com.clevertap.demo.ecom.databinding.FragmentCartBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartAdapter
    private var cleverTapDefaultInstance: CleverTapAPI? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        binding.include.toolbarText.text = "Cart"
        cleverTapDefaultInstance = MyApplication.getInstance().clevertap()
        
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(CartManager.getCartItems().toMutableList()) { product ->
            CartManager.removeFromCart(product)
            updateCartUI()
            
            // Track Removed from Cart Event
            val prodDetails = HashMap<String, Any>()
            prodDetails["Product Name"] = product.title
            prodDetails["Product ID"] = product.id
            prodDetails["Price"] = product.price
            cleverTapDefaultInstance?.pushEvent("Removed from Cart", prodDetails)
        }
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = cartAdapter
        updateCartUI()
    }

    private fun updateCartUI() {
        val items = CartManager.getCartItems()
        if (items.isEmpty()) {
            binding.emptyCartText.visibility = View.VISIBLE
            binding.cartRecyclerView.visibility = View.GONE
        } else {
            binding.emptyCartText.visibility = View.GONE
            binding.cartRecyclerView.visibility = View.VISIBLE
            cartAdapter.updateData(items)
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartUI()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CartFragment()
    }
}