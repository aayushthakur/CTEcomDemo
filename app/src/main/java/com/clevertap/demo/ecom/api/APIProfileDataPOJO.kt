package com.clevertap.demo.ecom.api

import com.google.gson.annotations.SerializedName


data class APIProfileDataPOJO(

    @SerializedName("preferredcategory") var preferredcategory: String? = null,
    @SerializedName("dob") var dob: String? = null,
    @SerializedName("itp") var itp: String? = null

)