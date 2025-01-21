package com.clevertap.demo.ecom

import android.app.Application
import android.app.NotificationManager
import android.util.Log
import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler
import com.clevertap.android.sdk.ActivityLifecycleCallback
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.interfaces.NotificationHandler
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging


class MyApplication : Application() {

    companion object Factory {
        private var INSTANCE: MyApplication? = null
        fun getInstance() : MyApplication {
            return INSTANCE!!
        }
    }
    private var clevertap : CleverTapAPI? = null

    override fun onCreate() {
        ActivityLifecycleCallback.register(this);
        super.onCreate()
        FirebaseApp.initializeApp(this)
        INSTANCE = this
        clevertap = CleverTapAPI.getDefaultInstance(applicationContext)!!
        clevertap!!.enableDeviceNetworkInfoReporting(true)
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE)
        CleverTapAPI.setNotificationHandler(PushTemplateNotificationHandler() as NotificationHandler);
    }

    fun clevertap() : CleverTapAPI? {
        return clevertap
    }
}