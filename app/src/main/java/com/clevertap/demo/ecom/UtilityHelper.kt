package com.clevertap.demo.ecom

import android.content.Context
import android.content.SharedPreferences

class UtilityHelper private constructor() {

    companion object {
        val INSTANCE: UtilityHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { UtilityHelper() }
    }


    fun savePIIDataSharedPreference(context: Context, email:String, name:String?, phone:String?, categoryPreference:String?, dob:String?){
        val sharedPreference =  context.getSharedPreferences(Constants.PII,Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString(Constants.email,email)
        editor.putString(Constants.name,name)
        editor.putString(Constants.phone,phone)
        editor.putString(Constants.categoryPreference,categoryPreference)
        editor.putString(Constants.dob,dob)
        editor.apply()
    }

    fun getPIISavedDataSharedPreference(context: Context): SharedPreferences? {
        return  context.getSharedPreferences(Constants.PII,Context.MODE_PRIVATE)
    }


}