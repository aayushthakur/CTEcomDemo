package com.clevertap.demo.ecom

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import com.google.common.base.Objects
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
    lateinit var fintechTheme: Var<String>


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

        val industry =
            UtilityHelper.INSTANCE.getIndustrySelectionSharedPreference(applicationContext)
                ?.getString(Constants.INDUSTRY,Constants.ECOMMERCE)

        if (industry.equals(Constants.ECOMMERCE)){
            festivalTheme = cleverTapDefaultInstance.defineVariable(
                "festival_theme","valentines")

            festivalTheme.addValueChangedCallback(object : VariableCallback<String>() {
                override fun onValueChanged(varInstance: Var<String>) {
                    varInstance.let { validInstance ->
                        applicationContext?.let {
                            Handler(it.mainLooper).post {
                                festivalTheme = validInstance
                                var imageUrl ="https://iili.io/2yFd6D7.jpg"
                                if (festivalTheme.stringValue.equals("holi")){
                                    imageUrl ="https://iili.io/2yFdix9.jpg"
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

        }else if (industry.equals(Constants.FINTECH)){
            fintechTheme = cleverTapDefaultInstance.defineVariable(
                "fintech_theme","https://iili.io/3Fo7y9R.jpg")

            fintechTheme.addValueChangedCallback(object : VariableCallback<String>() {
                override fun onValueChanged(varInstance: Var<String>) {
                    varInstance.let { validInstance ->
                        applicationContext?.let {
                            Handler(it.mainLooper).post {
                                fintechTheme = validInstance
                                var imageUrl =fintechTheme.stringValue
                                Log.d(TAG, "onValueChanged() called with $validInstance $fintechTheme $imageUrl")
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

        }



        cleverTapDefaultInstance.fetchVariables { isSuccess ->
            // isSuccess is true when server request is successful, false otherwise
            if (isSuccess) {
                if (industry.equals(Constants.ECOMMERCE)){
                    festivalTheme =
                        cleverTapDefaultInstance.getVariable("festival_theme")
                }else if (industry.equals(Constants.FINTECH)){
                    fintechTheme =
                        cleverTapDefaultInstance.getVariable("fintech_theme")

                }

            }
        }


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

//        showCustomNotification(applicationContext)
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
                    val data: HashMap<String, Any> = hashMapOf(
                        "MSG-push" to true
                    )
                    Log.d(TAG, "push permission update() called  $data")
                    CTAnalyticsHelper.INSTANCE.pushProfile(data)
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


//    fun showCustomNotification(context: Context) {
//        val notificationLayout = RemoteViews(context.packageName, R.layout.custom_notif_layout_timer)
//
//        // Set values dynamically if needed
//        notificationLayout.setTextViewText(R.id.timer_text, "02:51:06")
//
//        val intent = Intent(context, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        val notification = NotificationCompat.Builder(context, "default_channel")
//            .setSmallIcon(R.drawable.notif_icon)
//            .setCustomContentView(notificationLayout)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        val notificationManager = NotificationManagerCompat.from(context)
//        notificationManager.notify(1001, notification)
//    }


    /*  override fun onDisplayUnitsLoaded(units: ArrayList<CleverTapDisplayUnit>?) {
          Log.d(TAG, "onDisplayUnitsLoaded() called with: units = [" + units.toString() + "]");
          mListener?.loadData(units!!)
      }*/
}