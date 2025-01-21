package com.clevertap.demo.ecom.mainFragments

import com.clevertap.demo.ecom.Constants
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.clevertap.android.sdk.displayunits.DisplayUnitListener
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit
import com.clevertap.demo.ecom.CTAnalyticsHelper
import com.clevertap.demo.ecom.MainActivity
import com.clevertap.demo.ecom.MyApplication
import com.clevertap.demo.ecom.R
import com.clevertap.demo.ecom.carousel.CarouselAdapter
import com.clevertap.demo.ecom.carousel.ImageModel
import com.clevertap.demo.ecom.carousel.POJOCarouselImageModel
import com.clevertap.demo.ecom.databinding.FragmentHomeBinding
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), FragmentCommunicator, DisplayUnitListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG: String = HomeFragment::class.java.simpleName
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var carouseImageList: ArrayList<ImageModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.include.toolbarText.text = "Home"

        val rv = binding.categoriesRecyclerView
        carouseImageList = ArrayList()
        // initialize the adapter,
        // and pass the required argument
        carouselAdapter = CarouselAdapter(requireActivity().applicationContext, carouseImageList)

        rv.adapter = carouselAdapter
        rv.layoutManager = LinearLayoutManager(context , HORIZONTAL,false)
        rv.setHasFixedSize(true)

        carouselAdapter.setOnItemClickListener(object : CarouselAdapter.OnItemClickListener {
            override fun onClick(imageView: ImageView?, url: String?, redirectUrl: String?) {
                //Do something like opening the image in new activity or showing it in full screen or something else.
            }
        })

        if (activity is MainActivity) {
            (activity as MainActivity?)?.setFragmentListener(this)
        }

        CTAnalyticsHelper.INSTANCE.pushEvent("Home Page Viewed")
        MyApplication.getInstance().clevertap().apply {
            this!!.setDisplayUnitListener(this@HomeFragment)
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String = "", param2: String = "") = HomeFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun loadData(units: ArrayList<CleverTapDisplayUnit>) {
        Log.d(TAG, "loadData() called with: units = [$units]")
        for (cleverTapDisplayUnit in units) {
            //CustomKV
            val customMap = cleverTapDisplayUnit.customExtras ?: return
            Log.d(TAG, "loadData() called with: units = $customMap")
            if (customMap.containsKey("native_display_type") && customMap["native_display_type"] == "home_carousel_banner") {/*for (Map.Entry<String,String> entry : customMap.entrySet()) {
                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                }*//* val customImage1 : String = customMap["carousel_image_1"]!!
                val customImage2 : String = customMap["carousel_image_2"]!!
                val customImage3 : String = customMap["carousel_image_3"]!!
                val list = arrayListOf<ImageModel>()
                list.add(ImageModel(customImage1))
                list.add(ImageModel(customImage2))
                list.add(ImageModel(customImage3))

                Log.d(TAG, ("onDisplayUnitsLoaded() called with: carousel models = [" + list.size + "]"))
                carouselAdapter.updateList(list)
                carouselAdapter.notifyDataSetChanged()*/

            } else {
                //backend
            }
           /* if (units != null) {
                for (cleverTapDisplayUnit in units) {
                    //CustomKV
                    val customMap = cleverTapDisplayUnit.customExtras ?: return
                    Log.d(TAG, "onDisplayUnitsLoaded() called with: units = $customMap")
                    if (customMap.containsKey(com.clevertap.demo.ecom.Constants.ND_TYPE) && customMap[com.clevertap.demo.ecom.Constants.ND_TYPE] == com.clevertap.demo.ecom.Constants.HOME_CAROUSEL_BANNER && customMap.containsKey(
                            com.clevertap.demo.ecom.Constants.PAYLOAD
                        ) && customMap[com.clevertap.demo.ecom.Constants.PAYLOAD] != null
                    ) {
                        val gson = Gson().fromJson(
                            customMap[com.clevertap.demo.ecom.Constants.PAYLOAD].toString(), POJOCarouselImageModel::class.java
                        )
                        for (data in gson.carouselImage) {
                            carouseImageList.add(
                                ImageModel(
                                    data.imageUrl,
                                    data.imageName,
                                    data.imageRedirectUrl,
                                    data.imageOrder
                                )
                            )
                        }
                        carouselAdapter.updateList(carouseImageList)
                        carouselAdapter.notifyDataSetChanged()
                    } else {
                        //backend
                    }
                }
            }*/
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDisplayUnitsLoaded(units: java.util.ArrayList<CleverTapDisplayUnit>?) {
        Log.d(TAG, "onDisplayUnitsLoaded() called with: units = [$units]")
        if (units != null) {
            for (cleverTapDisplayUnit in units) {
                //CustomKV
                val customMap = cleverTapDisplayUnit.customExtras ?: return
                Log.d(TAG, "onDisplayUnitsLoaded() called with: units = $customMap")
                if (customMap.containsKey(Constants.ND_TYPE) && customMap[Constants.ND_TYPE] == Constants.HOME_CAROUSEL_BANNER && customMap.containsKey(
                        Constants.PAYLOAD
                    ) && customMap[Constants.PAYLOAD] != null
                ) {
                    val gson = Gson().fromJson(
                        customMap[Constants.PAYLOAD].toString(), POJOCarouselImageModel::class.java
                    )
                    val imageList = ArrayList<SlideModel>()
                    for (data in gson.carouselImage) {
                        imageList.add(
                            SlideModel(
                                data.imageUrl
                            )
                        )
                        carouseImageList.add(
                            ImageModel(
                                data.imageUrl,
                                data.imageName,
                                data.imageRedirectUrl,
                                data.imageOrder
                            )
                        )
                    }
                    binding.carouselBannerRecyclerView.setImageList(imageList, ScaleTypes.FIT)
                    binding.carouselBannerRecyclerView.startSliding()
                    binding.carouselBannerRecyclerView.setItemClickListener(object : ItemClickListener {
                        override fun onItemSelected(position: Int) {
                            // You can listen here.
                            Toast.makeText(context,"You have clicked on Image No : ${carouseImageList[position].order}", Toast.LENGTH_SHORT).show()
                        }
                    })

                } else {
                    //backend
                }
            }
        }
    }

}