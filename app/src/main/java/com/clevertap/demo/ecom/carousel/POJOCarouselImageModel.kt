package com.clevertap.demo.ecom.carousel

import com.google.gson.annotations.SerializedName

data class POJOCarouselImageModel (

    @SerializedName("carousel_images" ) var carouselImage : ArrayList<POJOCarouselImageListModel> = arrayListOf()

)