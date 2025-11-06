package com.clevertap.demo.ecom.productExperiences

import com.google.gson.annotations.SerializedName

data class HomePage (

    @SerializedName("bottom_strip" ) var bottomStrip : BottomStrip? = BottomStrip()

)