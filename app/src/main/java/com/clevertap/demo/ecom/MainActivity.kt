package com.clevertap.demo.ecom

import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.PushPermissionResponseListener
import com.clevertap.android.sdk.inapp.CTLocalInApp
import com.clevertap.android.sdk.variables.Var
import com.clevertap.android.sdk.variables.callbacks.VariableCallback
import com.clevertap.demo.ecom.api.APIResultPOJO
import com.clevertap.demo.ecom.api.ApiInterface
import com.clevertap.demo.ecom.databinding.ActivityMainBinding
import com.clevertap.demo.ecom.mainFragments.AccountFragment
import com.clevertap.demo.ecom.mainFragments.CartFragment
import com.clevertap.demo.ecom.mainFragments.FavouriteFragment
import com.clevertap.demo.ecom.mainFragments.FragmentCommunicator
import com.clevertap.demo.ecom.mainFragments.HomeFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), PushPermissionResponseListener {

    private lateinit var cleverTapDefaultInstance: CleverTapAPI
    private lateinit var binding: ActivityMainBinding
    private val TAG: String = MainActivity::class.java.simpleName
    var mListener: FragmentCommunicator? = null

    private var homeFragment: HomeFragment? = null
    private var cartFragment: CartFragment? = null
    private var favouriteFragment: FavouriteFragment? = null
    private var accountFragment: AccountFragment? = null
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment? = homeFragment
    private lateinit var apiInterface: ApiInterface
    lateinit var festivalTheme: Var<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding.include.toolbarText.text = "Home"

        homeFragment = HomeFragment.newInstance()
        cartFragment = CartFragment.newInstance()
        favouriteFragment = FavouriteFragment.newInstance()
        accountFragment = AccountFragment.newInstance()
        activeFragment = homeFragment

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        cleverTapDefaultInstance = MyApplication.getInstance().clevertap()

        festivalTheme = cleverTapDefaultInstance.defineVariable(
            "festival_theme","valentines")

        cleverTapDefaultInstance.fetchVariables { isSuccess ->
            // isSuccess is true when server request is successful, false otherwise
            if (isSuccess) {
                festivalTheme =
                    cleverTapDefaultInstance.getVariable("festival_theme")
            }
        }

        festivalTheme.addValueChangedCallback(object : VariableCallback<String>() {
            override fun onValueChanged(varInstance: Var<String>) {
                varInstance.let { validInstance ->
                    applicationContext?.let {
                        Handler(it.mainLooper).post {
                            festivalTheme = validInstance
                            var imageUrl ="https://iili.io/2tfZUNf.jpg"
                            if (festivalTheme.stringValue.equals("holi")){
                                 imageUrl ="https://iili.io/2tqejf4.jpg"
                            }
                            Log.d(TAG, "onValueChanged() called with $validInstance $festivalTheme $imageUrl")
                            // run code
                            Picasso.get()
                                .load(imageUrl)
                                .into(object : com.squareup.picasso.Target {
                                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                        binding.main.background = BitmapDrawable(resources, bitmap)
                                    }

                                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: android.graphics.drawable.Drawable?) {
                                        // Handle error case
                                        Log.d(
                                            TAG,
                                            "onBitmapFailed() called with: e = $e, errorDrawable = $errorDrawable"
                                        )
                                    }

                                    override fun onPrepareLoad(placeHolderDrawable: android.graphics.drawable.Drawable?) {
                                        // Optional: You can set a placeholder here
                                    }
                                })
                        }
                    }
                }
            }
        })

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    fragmentManager.beginTransaction().hide(activeFragment!!).show(homeFragment!!)
                        .commit()
                    activeFragment = homeFragment
                    true
                }

                R.id.cart -> {
                    fragmentManager.beginTransaction().hide(activeFragment!!).show(cartFragment!!)
                        .commit()
                    activeFragment = cartFragment
                    true
                }

                R.id.fav -> {
                    fragmentManager.beginTransaction().hide(activeFragment!!)
                        .show(favouriteFragment!!).commit()
                    activeFragment = favouriteFragment
                    true
                }

                R.id.profile -> {
                    fragmentManager.beginTransaction().hide(activeFragment!!)
                        .show(accountFragment!!).commit()
                    activeFragment = accountFragment
                    true
                }

                else -> false
            }
        }

        //setting default fragment as Home
        loadFragment(activeFragment!!)

        cleverTapDefaultInstance.registerPushPermissionNotificationResponseListener(this)

        val builder = CTLocalInApp.builder()
            .setInAppType(CTLocalInApp.InAppType.ALERT)
            .setTitleText("Get Notified")
            .setMessageText("Enable Notification permission")
            .followDeviceOrientation(true)
            .setPositiveBtnText("Allow")
            .setNegativeBtnText("Cancel")
            .build()
        cleverTapDefaultInstance.promptPushPrimer(builder)

//        apiInterface = RetrofitInstance.getInstance().create(ApiInterface::class.java)
//        val prefEmail = UtilityHelper.INSTANCE.getPIISavedDataSharedPreference(applicationContext)
//            ?.getString(Constants.email,"");
//        val prefName = UtilityHelper.INSTANCE.getPIISavedDataSharedPreference(applicationContext)
//            ?.getString(Constants.name,"");
//        if (prefEmail != null && TextUtils.isEmpty(prefName)) {
//            getProfileDataViaEmail(prefEmail)
//        }
    }

    private fun getProfileDataViaEmail(email: String){
        val call = apiInterface.getProfileViaEmail(email,UtilityHelper.INSTANCE.getHeaderMap())
        call.enqueue(object : Callback<APIResultPOJO> {
            override fun onResponse(call: Call<APIResultPOJO>, response: Response<APIResultPOJO>) {
                if (response.isSuccessful && response.body()!=null){
                    val recordPojo = response.body()!!.record
                    Log.d(TAG, "onResponse() called with: response = $recordPojo")
                    if (recordPojo!=null){
                        val name = recordPojo.name
                        val email = recordPojo.email
                        val profileData = recordPojo.profileData
                        var dob = ""
                        var preferredCategory =""
                        if (profileData!=null){
                            dob = profileData.dob!!
                            preferredCategory = profileData.preferredcategory!!
                        }
                        if (!TextUtils.isEmpty(dob)){
                            // Extract the value after the underscore
                            val epochTimeString = dob.substringAfter("_")
                            // Convert the extracted string to a Long (epoch time in seconds)
                            val epochTime = epochTimeString.toLong()
                            dob = UtilityHelper.INSTANCE.getDateTime(epochTime)
                        }

                        val platformInfo = recordPojo.platformInfo
                        var phone = ""
                        if (platformInfo.isNotEmpty()){
                            for (data in platformInfo){
                                if (!TextUtils.isEmpty(data.phone)){
                                    phone = data.phone!!
                                    break
                                }
                            }
                        }
                        Log.d(TAG, "onResponse() called with: name = $name email = $email preferredCategory = $preferredCategory dob =  $dob  phone = $phone")
                        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(name)
                            && !TextUtils.isEmpty(phone)
                            && !TextUtils.isEmpty(preferredCategory) && !TextUtils.isEmpty(dob)){

                            UtilityHelper.INSTANCE.savePIIDataSharedPreference(applicationContext,
                                email!!,name, "+$phone",preferredCategory,dob)
                        }

                    }
                }
            }

            override fun onFailure(call: Call<APIResultPOJO>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun loadFragment(fragment: Fragment) {/* val transaction = supportFragmentManager.beginTransaction()
         transaction.replace(binding.frameLayout.id, fragment)
         transaction.commit()*/

        supportFragmentManager.beginTransaction().apply {
            add(binding.frameLayout.id, homeFragment!!, "Home")
            add(binding.frameLayout.id, cartFragment!!, "Cart").hide(cartFragment!!)
            add(binding.frameLayout.id, favouriteFragment!!, "Favourite").hide(favouriteFragment!!)
            add(binding.frameLayout.id, accountFragment!!, "Account").hide(accountFragment!!)
        }.commit()
    }

    fun setFragmentListener(fragmentCommunicator: FragmentCommunicator) {
        mListener = fragmentCommunicator
    }

    override fun onPushPermissionResponse(p0: Boolean) {
        if (p0) {

            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // fetching the token
                val token = task.result
                if (TextUtils.isEmpty(token)) {
                    cleverTapDefaultInstance.pushFcmRegistrationId(token, true)
                }

            })
            CleverTapAPI.createNotificationChannel(
                applicationContext, "default_channel", "Default Channel",
                "Default Channel", NotificationManager.IMPORTANCE_MAX, true
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cleverTapDefaultInstance.unregisterPushPermissionNotificationResponseListener(this)
    }


    /*  override fun onDisplayUnitsLoaded(units: ArrayList<CleverTapDisplayUnit>?) {
          Log.d(TAG, "onDisplayUnitsLoaded() called with: units = [" + units.toString() + "]");
          mListener?.loadData(units!!)
      }*/
}