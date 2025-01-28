package com.clevertap.demo.ecom

import android.content.Context
import android.content.SharedPreferences
import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

class UtilityHelper private constructor() {

    companion object {
        val INSTANCE: UtilityHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { UtilityHelper() }
    }


    fun isValidPhoneNumber(phoneNumber: String) : Boolean {
        val REG = "^[+]{1}(?:[0-9\\-\\(\\)\\/" +
                "\\.]\\s?){6,15}[0-9]{1}$"
        return Pattern.matches(REG,phoneNumber)
    }
    fun isValidEmail(email: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
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

    fun getHeaderMap(): Map<String, String> {
        val headerMap = mutableMapOf<String, String>()
        headerMap["X-CleverTap-Account-Id"] = "6KZ-ZKK-6K7Z"
        headerMap["X-CleverTap-Passcode"] = "WHK-ZWB-OLEL"
        headerMap["Content-Type"] = "application/json"
        return headerMap
    }

     fun getDateTime(epochTime: Long): String {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(epochTime * 1000)
            return sdf.format(date)
        } catch (e: Exception) {
            return e.toString()
        }
    }


}