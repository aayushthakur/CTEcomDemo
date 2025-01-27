package com.clevertap.demo.ecom

import android.text.TextUtils
import android.util.Log
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if the message contains data
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            CTFcmMessageHandler()
                .createNotification(applicationContext, remoteMessage)
        }

//        // Check if the message contains a notification payload
//        remoteMessage.notification?.let {
//            Log.d(TAG, "Message Notification Body: ${it.body}")
//        }
    }

    override fun onNewToken(token: String) {
        // Handle new or refreshed FCM registration token
        Log.d(TAG, "Refreshed token: $token")
        // You may want to send this token to your server for further use
        if (TextUtils.isEmpty(token)) {
            MyApplication.getInstance().clevertap()?.pushFcmRegistrationId(token, true)
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}