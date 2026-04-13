package com.clevertap.demo.ecom

import android.app.Application
import android.app.NotificationManager
import android.util.Log
import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler
import com.clevertap.android.sdk.ActivityLifecycleCallback
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.inapp.customtemplates.function
import com.clevertap.android.sdk.inapp.customtemplates.template
import com.clevertap.android.sdk.interfaces.NotificationHandler
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import java.util.HashMap


class MyApplication : Application(), CTPushNotificationListener {

    companion object Factory {
        private var INSTANCE: MyApplication? = null
        fun getInstance(): MyApplication {
            return INSTANCE!!
        }
    }

    private lateinit var clevertap: CleverTapAPI

    override fun onCreate() {
        ActivityLifecycleCallback.register(this)
        super.onCreate()

        //this needs to be called before CleverTapAPI.getDefaultInstance()
        CleverTapAPI.registerCustomInAppTemplates {
            setOf(
                function(isVisual = false) {   // Check Visual vs Non-visual Functions in "Key Differences"
                    name("function")
                    presenter(MyFunctionPresenter())
                    stringArgument("message", "Hello")
                }
            )
        }
        //Clevertap Event Here
        FirebaseApp.initializeApp(this)
        INSTANCE = this
        clevertap = CleverTapAPI.getDefaultInstance(applicationContext)!!
        clevertap.enableDeviceNetworkInfoReporting(true)
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE)
        CleverTapAPI.setNotificationHandler(PushTemplateNotificationHandler() as NotificationHandler)
        clevertap.ctPushNotificationListener = this


//        clevertap.syncRegisteredInAppTemplates()
    }

    override fun onNotificationClickedPayloadReceived(p0: HashMap<String, Any>?) {
        //
    }

    fun clevertap(): CleverTapAPI {
        return clevertap
    }
}