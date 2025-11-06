package com.clevertap.demo.ecom.productExperiences

import com.google.gson.annotations.SerializedName

data class BottomStripPOJO (
    @SerializedName("home_page" ) var homePage : ArrayList<HomePage> = arrayListOf()
)