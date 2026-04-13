package com.clevertap.demo.ecom

import android.util.Log
import android.widget.Toast
import com.clevertap.android.sdk.inapp.customtemplates.CustomTemplateContext
import com.clevertap.android.sdk.inapp.customtemplates.FunctionPresenter


class MyFunctionPresenter : FunctionPresenter {
    private val TAG: String = MyFunctionPresenter::class.java.simpleName

    override fun onPresent(context: CustomTemplateContext.FunctionContext) {
        Log.d(TAG, "onPresent() called with: context = $context")
        showMessageOnApp( message = context.getString("message"))
        context.setPresented()
    }

    fun showMessageOnApp(message: String?){
        Log.d(TAG, "showMessageOnApp() called with: message = $message")
        Toast.makeText(MyApplication.getInstance().applicationContext, message, Toast.LENGTH_SHORT).show()

    }
}