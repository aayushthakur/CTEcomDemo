package com.clevertap.demo.ecom

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.clevertap.android.sdk.CleverTapAPI
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
import com.clevertap.demo.ecom.productExperiences.BottomStripPOJO
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity()/*, PushPermissionResponseListener*/ {

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
    lateinit var orderDeliveryStripInstance: Var<String>
    lateinit var fintechTheme: Var<String>
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private lateinit var picassoTarget: Target


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

        loadPEData()

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

       /* requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
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

                Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
//            Toast.makeText(
//                this, "${getString(R.string.app_name)} can't post notifications without Notification permission",
//                Toast.LENGTH_LONG
//            ).show()

                Snackbar.make(
                    binding.main,
                    String.format(
                        String.format(
                            "You Denied Push Permission before, Please click on Settings and allow the push permission",
                            getString(R.string.app_name)
                        )
                    ),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Settings") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        startActivity(settingsIntent)
                    }
                }.show()
            }
        }

        askNotificationPermission()*/

        /*cleverTapDefaultInstance.registerPushPermissionNotificationResponseListener(this)

        val builder = CTLocalInApp.builder()
            .setInAppType(CTLocalInApp.InAppType.ALERT)
            .setTitleText("Get Notified")
            .setMessageText("Enable Notification permission")
            .followDeviceOrientation(true)
            .setPositiveBtnText("Allow")
            .setNegativeBtnText("Cancel")
            .build()
        cleverTapDefaultInstance.promptPushPrimer(builder)*/

//        apiInterface = RetrofitInstance.getInstance().create(ApiInterface::class.java)
//        val prefEmail = UtilityHelper.INSTANCE.getPIISavedDataSharedPreference(applicationContext)
//            ?.getString(Constants.email,"");
//        val prefName = UtilityHelper.INSTANCE.getPIISavedDataSharedPreference(applicationContext)
//            ?.getString(Constants.name,"");
//        if (prefEmail != null && TextUtils.isEmpty(prefName)) {
//            getProfileDataViaEmail(prefEmail)
//        }

//        showCustomNotification(applicationContext)

        checkNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        loadPEData()
    }

    private fun loadPEData(){
        val industry =
            UtilityHelper.INSTANCE.getIndustrySelectionSharedPreference(applicationContext)
                ?.getString(Constants.INDUSTRY, Constants.ECOMMERCE)

        if (industry.equals(Constants.ECOMMERCE)) {
            festivalTheme = cleverTapDefaultInstance.defineVariable(
                "festival_theme", "valentines"
            )

            orderDeliveryStripInstance = cleverTapDefaultInstance.defineVariable(
                "home_page", "{\n" +
                        "  \"home_page\": [\n" +
                        "    {\n" +
                        "      \"bottom_strip\": {\n" +
                        "        \"delivery_status\": \"On Time\",\n" +
                        "        \"delivery_date\": \"26/07/2025\",\n" +
                        "        \"text_color\": \"#ffffff\",\n" +
                        "        \"background_color\": \"#000000\",\n" +
                        "        \"to_show\": false,\n" +
                        "        \"deep_link\": \"www.google.com\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"
            )

            festivalTheme.addValueChangedCallback(object : VariableCallback<String>() {
                override fun onValueChanged(varInstance: Var<String>) {
                    varInstance.let { validInstance ->
                        applicationContext?.let {
                            Handler(it.mainLooper).post {
                                festivalTheme = validInstance
                                var imageUrl = "https://db1f5xelmebpj.cloudfront.net/1642491392/assets/e752ee9bdcae49c7a9309aeefeab8539.jpeg"
                                if (festivalTheme.stringValue.equals("holi")) {
                                    imageUrl = "https://db1f5xelmebpj.cloudfront.net/1642491392/assets/2c3a0345f9fa41a6b3bb96c6198e0fc8.jpeg"
                                }
                                Log.d(
                                    TAG,
                                    "onValueChanged() called with $validInstance $festivalTheme $imageUrl"
                                )

                                // 2. Initialize the Target object
                                picassoTarget = object : Target {
                                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                        Log.d("Picasso", "Bitmap loaded successfully")

                                        // Set the bitmap as the background
                                        binding.main.background = BitmapDrawable(resources, bitmap)
                                    }

                                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                                        Log.e("Picasso", "Failed to load bitmap", e)
                                        // Optionally set a fallback background
                                    }

                                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                                        Log.d("Picasso", "Preparing to load image")
                                        // Optionally set a placeholder background
                                    }
                                }

                                // 3. Start the Picasso request, loading into your Target
                                Picasso.get()
                                    .load(imageUrl)
                                    .into(picassoTarget)
                                // run code
//                                Picasso.get()
//                                    .load(imageUrl)
//                                    .into(object : Target {
//                                        override fun onBitmapLoaded(
//                                            bitmap: Bitmap?,
//                                            from: Picasso.LoadedFrom?
//                                        ) {
//                                            binding.main.background =
//                                                BitmapDrawable(resources, bitmap)
//                                        }
//
//                                        override fun onBitmapFailed(
//                                            e: Exception?,
//                                            errorDrawable: Drawable?
//                                        ) {
//                                            // Handle error case
//                                            Log.d(
//                                                TAG,
//                                                "onBitmapFailed() called with: e = $e, errorDrawable = $errorDrawable"
//                                            )
//                                        }
//
//                                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
//                                            // Optional: You can set a placeholder here
//                                        }
//                                    })
                            }
                        }
                    }
                }
            })


            orderDeliveryStripInstance.addValueChangedCallback(object : VariableCallback<String>() {
                override fun onValueChanged(varInstance: Var<String>) {
                    varInstance.let { validInstance ->
                        applicationContext?.let {
                            Handler(it.mainLooper).post {
                                orderDeliveryStripInstance = validInstance
                                renderBottomStripData()
                            }
                        }
                    }
                }
            })

        } else if (industry.equals(Constants.FINTECH)) {
            fintechTheme = cleverTapDefaultInstance.defineVariable(
                "fintech_theme", "https://iili.io/3Fo7y9R.jpg"
            )

            fintechTheme.addValueChangedCallback(object : VariableCallback<String>() {
                override fun onValueChanged(varInstance: Var<String>) {
                    varInstance.let { validInstance ->
                        applicationContext?.let {
                            Handler(it.mainLooper).post {
                                fintechTheme = validInstance
                                var imageUrl = fintechTheme.stringValue
                                Log.d(
                                    TAG,
                                    "onValueChanged() called with $validInstance $fintechTheme $imageUrl"
                                )
                                // run code
                                Picasso.get()
                                    .load(imageUrl)
                                    .into(object : Target {
                                        override fun onBitmapLoaded(
                                            bitmap: Bitmap?,
                                            from: Picasso.LoadedFrom?
                                        ) {
                                            binding.main.background =
                                                BitmapDrawable(resources, bitmap)
                                        }

                                        override fun onBitmapFailed(
                                            e: Exception?,
                                            errorDrawable: Drawable?
                                        ) {
                                            // Handle error case
                                            Log.d(
                                                TAG,
                                                "onBitmapFailed() called with: e = $e, errorDrawable = $errorDrawable"
                                            )
                                        }

                                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
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
                if (industry.equals(Constants.ECOMMERCE)) {
                    festivalTheme =
                        cleverTapDefaultInstance.getVariable("festival_theme")
                } else if (industry.equals(Constants.FINTECH)) {
                    fintechTheme =
                        cleverTapDefaultInstance.getVariable("fintech_theme")

                }

            }
        }
    }

    private fun checkNotificationPermission() {
        // Check for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    showToast("Permission Granted")
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
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // User previously denied permission, show soft popup
                    showSoftPopup()
                }
                else -> {
                    // Directly request permission
                    requestNotificationPermission()
                }
            }
        } else {
            // For versions below Android 13, no runtime permission required
            showToast("Permission Granted (Below Android 13)")
        }
    }

    // Soft popup explaining why permission is needed
    private fun showSoftPopup() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("We need notification permission to send you updates.")
            .setPositiveButton("OK") { _, _ ->
                requestNotificationPermission()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Hard permission request
    private fun requestNotificationPermission() {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    // Launcher to handle permission result
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                showToast("Permission Granted")
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
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    showToast("Permission Denied")
                } else {
                    // Permission denied and cannot be asked again
                    showSettingsPopup()
                }
            }
        }

    // Popup directing to app settings
    private fun showSettingsPopup() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("You have denied permission. Please enable it from app settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Open app settings
    private fun openAppSettings() {
        /*val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)*/

        val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivity(settingsIntent)
    }

    // Show toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
//                Log.e(TAG, "PERMISSION_GRANTED")
                // FCM SDK (and your app) can post notifications.
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
            } else {
//                Log.e(TAG, "NO_PERMISSION")
                // Directly ask for the permission
                requestPermissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun renderBottomStripData(){
        val gson = Gson().fromJson(
            orderDeliveryStripInstance.stringValue, BottomStripPOJO::class.java
        )
//        for (data in gson.homePage) {
//            data.bottomStrip
//        }

        if (gson.homePage.size > 0){
            val data = gson.homePage.get(0).bottomStrip
            if (data!=null) {
                val deliveryDate = data.deliveryDate
                val deliveryStatus = data.deliveryStatus
                val toShow = data.toShow as Boolean
                val dl = data.deepLink
                val textColor = data.textColor
                val bgColor = data.backgroundColor

                if (toShow){
                    binding.orderDeliveryTextView.setTextColor(Color.parseColor(textColor))  // Coral
                    binding.orderDeliveryTextView.setBackgroundColor(Color.parseColor(bgColor))
                    binding.orderDeliveryTextView.setText("Hi Your order is $deliveryStatus & will reach by $deliveryDate")
                    binding.orderDeliveryTextView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getProfileDataViaEmail(email: String) {
        val call = apiInterface.getProfileViaEmail(email, UtilityHelper.INSTANCE.getHeaderMap())
        call.enqueue(object : Callback<APIResultPOJO> {
            override fun onResponse(call: Call<APIResultPOJO>, response: Response<APIResultPOJO>) {
                if (response.isSuccessful && response.body() != null) {
                    val recordPojo = response.body()!!.record
                    Log.d(TAG, "onResponse() called with: response = $recordPojo")
                    if (recordPojo != null) {
                        val name = recordPojo.name
                        val email = recordPojo.email
                        val profileData = recordPojo.profileData
                        var dob = ""
                        var preferredCategory = ""
                        var preferredTheme = ""
                        if (profileData != null) {
                            dob = profileData.dob!!
                            preferredCategory = profileData.preferredcategory!!
                            preferredTheme = profileData.preferredtheme!!
                        }
                        if (!TextUtils.isEmpty(dob)) {
                            // Extract the value after the underscore
                            val epochTimeString = dob.substringAfter("_")
                            // Convert the extracted string to a Long (epoch time in seconds)
                            val epochTime = epochTimeString.toLong()
                            dob = UtilityHelper.INSTANCE.getDateTime(epochTime)
                        }

                        val platformInfo = recordPojo.platformInfo
                        var phone = ""
                        if (platformInfo.isNotEmpty()) {
                            for (data in platformInfo) {
                                if (!TextUtils.isEmpty(data.phone)) {
                                    phone = data.phone!!
                                    break
                                }
                            }
                        }
                        Log.d(
                            TAG,
                            "onResponse() called with: name = $name email = $email preferredCategory = $preferredCategory dob =  $dob  phone = $phone"
                        )
                        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(name)
                            && !TextUtils.isEmpty(phone)
                            && !TextUtils.isEmpty(preferredCategory) && !TextUtils.isEmpty(preferredTheme) && !TextUtils.isEmpty(dob)
                        ) {

                            UtilityHelper.INSTANCE.savePIIDataSharedPreference(
                                applicationContext,
                                email!!, name, "+$phone", preferredCategory, preferredTheme,dob
                            )
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

    /*override fun onPushPermissionResponse(p0: Boolean) {
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
    }*/


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