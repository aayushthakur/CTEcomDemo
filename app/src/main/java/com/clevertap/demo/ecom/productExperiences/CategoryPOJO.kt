package com.clevertap.demo.ecom.productExperiences

import com.google.gson.annotations.SerializedName


data class CategoryPOJO(

    @SerializedName("name") var name: String? = null,
    @SerializedName("image_url") var imageUrl: String? = null,
    @SerializedName("redirect_url") var redirectUrl: String? = null,
    @SerializedName("order") var order: Int? = null

)