package com.clevertap.demo.ecom.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

interface ApiInterface {
    @GET("profile.json")
    fun getProfileViaEmail(@Query("email") emailId: String, @HeaderMap headers: Map<String, String>): Call<APIResultPOJO>
}