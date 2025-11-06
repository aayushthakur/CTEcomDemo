package com.clevertap.demo.ecom.productExperiences

import com.google.gson.annotations.SerializedName

data class BottomStrip (

    @SerializedName("delivery_status"  ) var deliveryStatus  : String?  = null,
    @SerializedName("delivery_date"    ) var deliveryDate    : String?  = null,
    @SerializedName("text_color"       ) var textColor       : String?  = null,
    @SerializedName("background_color" ) var backgroundColor : String?  = null,
    @SerializedName("to_show"          ) var toShow          : Boolean? = null,
    @SerializedName("deep_link"        ) var deepLink        : String?  = null

)