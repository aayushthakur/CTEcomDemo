package com.clevertap.demo.ecom

import android.app.NotificationManager
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.PushPermissionResponseListener
import com.clevertap.android.sdk.inapp.CTLocalInApp
import com.clevertap.demo.ecom.databinding.ActivityMainBinding
import com.clevertap.demo.ecom.mainFragments.AccountFragment
import com.clevertap.demo.ecom.mainFragments.CartFragment
import com.clevertap.demo.ecom.mainFragments.FavouriteFragment
import com.clevertap.demo.ecom.mainFragments.FragmentCommunicator
import com.clevertap.demo.ecom.mainFragments.HomeFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity(), PushPermissionResponseListener {

    private var clevertapInstance: CleverTapAPI? = null
    private lateinit var binding: ActivityMainBinding
    private val TAG: String = MainActivity::class.java.simpleName
    var mListener: FragmentCommunicator? = null

    private var homeFragment: HomeFragment? = null
    private var cartFragment: CartFragment? = null
    private var favouriteFragment: FavouriteFragment? = null
    private var accountFragment: AccountFragment? = null
    private val fragmentManager = supportFragmentManager
    private var activeFragment: Fragment? = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

        clevertapInstance = MyApplication.getInstance().clevertap()
        clevertapInstance?.registerPushPermissionNotificationResponseListener(this)

        val builder = CTLocalInApp.builder()
            .setInAppType(CTLocalInApp.InAppType.ALERT)
            .setTitleText("Get Notified")
            .setMessageText("Enable Notification permission")
            .followDeviceOrientation(true)
            .setPositiveBtnText("Allow")
            .setNegativeBtnText("Cancel")
            .build()
        clevertapInstance?.promptPushPrimer(builder)
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
                    clevertapInstance?.pushFcmRegistrationId(token, true)
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
        clevertapInstance?.unregisterPushPermissionNotificationResponseListener(this)
    }


    /*  override fun onDisplayUnitsLoaded(units: ArrayList<CleverTapDisplayUnit>?) {
          Log.d(TAG, "onDisplayUnitsLoaded() called with: units = [" + units.toString() + "]");
          mListener?.loadData(units!!)
      }*/
}