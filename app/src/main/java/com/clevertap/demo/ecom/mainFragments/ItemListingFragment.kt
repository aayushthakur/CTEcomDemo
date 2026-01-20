package com.clevertap.demo.ecom.mainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clevertap.demo.ecom.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class ItemListingFragment : Fragment() {

    companion object {
        private const val ARG_JSON_FILE_NAME = "json_file_name"

        fun newInstance(jsonFileName: String): ItemListingFragment {
            val fragment = ItemListingFragment()
            val args = Bundle()
            args.putString(ARG_JSON_FILE_NAME, jsonFileName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_item_listing, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.item_recycler_view)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.GONE

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val jsonFileName = arguments?.getString(ARG_JSON_FILE_NAME)
        if (jsonFileName != null) {
            val jsonString = getJsonDataFromAsset(jsonFileName)
            val gson = Gson()
            val listProductType = object : TypeToken<List<Product>>() {}.type
            val products: List<Product> = gson.fromJson(jsonString, listProductType)

            val adapter = ItemListingAdapter(products)
            recyclerView.adapter = adapter
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun getJsonDataFromAsset(fileName: String): String? {
        val jsonString: String
        try {
            jsonString = requireContext().assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
}