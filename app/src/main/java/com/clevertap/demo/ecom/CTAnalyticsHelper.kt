package com.clevertap.demo.ecom


class CTAnalyticsHelper private constructor() {

    companion object {
        val INSTANCE: CTAnalyticsHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { CTAnalyticsHelper() }
    }

    fun onUserLogin(map : HashMap<String, Any>){
        MyApplication.getInstance().clevertap()?.onUserLogin(map)
    }

    fun pushProfile(map : HashMap<String, Any>){
        MyApplication.getInstance().clevertap()?.pushProfile(map)
    }

    fun pushEvent(event : String, map: HashMap<String, Any> = HashMap() ){
        MyApplication.getInstance().clevertap()?.pushEvent(event, map)
    }

    fun pushChargedEvent(chargeDetails : HashMap<String,Any>, items: ArrayList<HashMap<String, Any>> = ArrayList() ){
        MyApplication.getInstance().clevertap()?.pushChargedEvent(chargeDetails, items)
    }
}