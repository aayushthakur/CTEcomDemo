package com.clevertap.demo.ecom.mainFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
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
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import com.clevertap.android.sdk.CTInboxListener
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.displayunits.DisplayUnitListener
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit
import com.clevertap.android.sdk.variables.Var
import com.clevertap.android.sdk.variables.callbacks.VariableCallback
import com.clevertap.demo.ecom.CTAnalyticsHelper
import com.clevertap.demo.ecom.Constants
import com.clevertap.demo.ecom.MainActivity
import com.clevertap.demo.ecom.MyApplication
import com.clevertap.demo.ecom.R
import com.clevertap.demo.ecom.carousel.CarouselBannerAdapter
import com.clevertap.demo.ecom.carousel.CategoriesAdapter
import com.clevertap.demo.ecom.carousel.CirclePagerIndicatorDecoration
import com.clevertap.demo.ecom.carousel.POJOCarouselImageModel
import com.clevertap.demo.ecom.databinding.FragmentHomeBinding
import com.clevertap.demo.ecom.productExperiences.HorizontalSpacingItemDecoration
import com.clevertap.demo.ecom.productExperiences.ImageModel
import com.clevertap.demo.ecom.productExperiences.TopCategoriesPOJO
import com.google.gson.Gson


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), FragmentCommunicator, DisplayUnitListener, CTInboxListener {
    private lateinit var cleverTapDefaultInstance: CleverTapAPI

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG: String = HomeFragment::class.java.simpleName
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var carouseImageList: ArrayList<ImageModel>
    private lateinit var categoriesList: ArrayList<ImageModel>
    lateinit var topCategories: Var<String>

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
        categoriesList = ArrayList()
        // initialize the adapter,
        // and pass the required argument
        categoriesAdapter = CategoriesAdapter(requireActivity().applicationContext, categoriesList)

        rv.adapter = categoriesAdapter
        rv.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        rv.setHasFixedSize(true)
        rv.addItemDecoration(
            HorizontalSpacingItemDecoration(
                resources.getDimensionPixelSize(R.dimen.category_spacing),
                0
            )
        )

        categoriesAdapter.setOnItemClickListener(object : CategoriesAdapter.OnItemClickListener {
            override fun onClick(imageView: ImageView?, url: String?, redirectUrl: String?) {
                //Do something like opening the image in new activity or showing it in full screen or something else.
            }
        })

        if (activity is MainActivity) {
            (activity as MainActivity?)?.setFragmentListener(this)
        }
        cleverTapDefaultInstance = MyApplication.getInstance().clevertap()
        CTAnalyticsHelper.INSTANCE.pushEvent("Home Page Viewed")

        cleverTapDefaultInstance.apply {
            this.setDisplayUnitListener(this@HomeFragment)
        }

        //Set the Notification Inbox Listener
        cleverTapDefaultInstance.ctNotificationInboxListener = this@HomeFragment
        //Initialize the inbox and wait for callbacks on overridden methods
        cleverTapDefaultInstance.initializeInbox()

        topCategories = cleverTapDefaultInstance.defineVariable(
            "top_categories",
            "{\"top_categories\":[{\"name\":\"Electronics\",\"image_url\":\"https://lh3.googleusercontent.com/d/1r5lyfdRNgArYVTqLidZ18QcfEiLiJmW7\",\"redirect_url\":\"\",\"order\":1},{\"name\":\"Mobile Phones\",\"image_url\":\"https://lh3.googleusercontent.com/d/12AuvIQ4361wTwxbLUAhx-eOD8qmWImtB\",\"redirect_url\":\"\",\"order\":2},{\"name\":\"Fashion\",\"image_url\":\"https://lh3.googleusercontent.com/d/13YcO3qvkjM3o5I6nH2dStpIQkvw4GJnJ\",\"redirect_url\":\"\",\"order\":3},{\"name\":\"Home & Kitchen\",\"image_url\":\"https://lh3.googleusercontent.com/d/1Xmkz060pg1Z_CU_bp6-7CaRWmHXmnN6Y\",\"redirect_url\":\"\",\"order\":4},{\"name\":\"Health\",\"image_url\":\"https://lh3.googleusercontent.com/d/1_5eJPxNbV_AmfxsBP0hpTjcytgi8xLQf\",\"redirect_url\":\"\",\"order\":5},{\"name\":\"Gift Cards\",\"image_url\":\"https://lh3.googleusercontent.com/d/1AbL31Jfhm-6veBrEzvu_cUX5JqgTTWLk\",\"redirect_url\":\"\",\"order\":6},{\"name\":\"Groceries\",\"image_url\":\"https://lh3.googleusercontent.com/d/1QaeypE-4qfq_x_E23lAO4ppZmmu6JQA6\",\"redirect_url\":\"\",\"order\":7}]}"
        )

        cleverTapDefaultInstance.fetchVariables { isSuccess ->
            // isSuccess is true when server request is successful, false otherwise
            if (isSuccess) {
                topCategories =
                    cleverTapDefaultInstance.getVariable("top_categories")
            }
        }

        topCategories.addValueChangedCallback(object : VariableCallback<String>() {
            override fun onValueChanged(varInstance: Var<String>) {
                Handler(context!!.mainLooper).post {
                    topCategories = varInstance
                    // run code
                    renderData()
                }
            }
        })
        return binding.root
    }


    fun renderData() {
        categoriesList.clear()
        categoriesAdapter.clearList()
        val gson = Gson().fromJson(
            topCategories.stringValue, TopCategoriesPOJO::class.java
        )
        for (data in gson.topCategories) {
            categoriesList.add(
                ImageModel(
                    data.imageUrl,
                    data.name,
                    data.redirectUrl,
                    data.order
                )
            )
        }

        var sortedList = categoriesList.sortedWith(compareBy { it.order })
        categoriesAdapter.updateList(sortedList)
        categoriesAdapter.notifyDataSetChanged()
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
                    carouseImageList.clear()
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
                    val carouselAdapter = CarouselBannerAdapter(carouseImageList)
                    binding.carouselBannerRecyclerView.run {
                        adapter = carouselAdapter
                        layoutManager =  LinearLayoutManager(context, HORIZONTAL, false)
                        hasFixedSize()
                        addItemDecoration(HorizontalSpacingItemDecoration(
                            resources.getDimensionPixelSize(R.dimen.category_spacing),
                            0
                        ))
                        addItemDecoration(CirclePagerIndicatorDecoration())
                        LinearSnapHelper()
                    }
                    carouselAdapter.setOnItemClickListener(object :
                        CarouselBannerAdapter.OnItemClickListener {
                        override fun onClick(
                            imageView: ImageView?,
                            url: String?,
                            redirectUrl: String?,
                            order:Int?
                        ) {
                            Toast.makeText(
                                 context,
                                 "You have clicked on Image No : $order}",
                                 Toast.LENGTH_SHORT
                             ).show()
                        }

                    })

//                     binding.carouselBannerRecyclerView.setItemSelectListener(object : OnSelected {
//                         override fun onItemSelected(position: Int) {
//                             Toast.makeText(
//                                 context,
//                                 "You have clicked on Image No : ${carouseImageList[position].order}",
//                                 Toast.LENGTH_SHORT
//                             ).show()
//                         }
//                     })

                } else {
                    //backend
                }
            }
        }
    }

    override fun inboxDidInitialize() {
        binding.include.toolbarNotifications.setOnClickListener(View.OnClickListener {
//            val inboxTabs =
//                arrayListOf("Promotions", "Offers", "Others")//Anything after the first 2 will be ignored
//            CTInboxStyleConfig().apply {
//                tabs = inboxTabs //Do not use this if you don't want to use tabs
//                tabBackgroundColor = "#FF0000"
//                selectedTabIndicatorColor = "#0000FF"
//                selectedTabColor = "#000000"
//                unselectedTabColor = "#FFFFFF"
//                backButtonColor = "#FF0000"
//                navBarTitleColor = "#FF0000"
//                navBarTitle = "MY INBOX"
//                navBarColor = "#FFFFFF"
//                inboxBackgroundColor = "#00FF00"
//                firstTabTitle = "First Tab"
//                cleverTapDefaultInstance?.showAppInbox(this) //Opens activity With Tabs
//            }
            //OR
            cleverTapDefaultInstance.showAppInbox()
        })
    }

    override fun inboxMessagesDidUpdate() {

    }

}