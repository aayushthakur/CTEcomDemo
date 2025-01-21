package com.clevertap.demo.ecom.carousel


import com.google.gson.annotations.SerializedName


data class POJOCarouselImageListModel(

    @SerializedName("image_name") var imageName: String? = null,
    @SerializedName("image_url") var imageUrl: String? = null,
    @SerializedName("image_redirect_url") var imageRedirectUrl: String? = null,
    @SerializedName("image_order") var imageOrder: Int? = null

)