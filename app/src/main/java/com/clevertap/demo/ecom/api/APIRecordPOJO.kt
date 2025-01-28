package com.clevertap.demo.ecom.api

import com.google.gson.annotations.SerializedName


data class APIRecordPOJO(

    @SerializedName("platformInfo") var platformInfo: ArrayList<APIPlatformInfoPOJO> = arrayListOf(),
    @SerializedName("identity") var identity: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
//  @SerializedName("all_identities" ) var allIdentities : ArrayList<String>       = arrayListOf(),
//  @SerializedName("events"         ) var events        : Events?                 = Events(),
    @SerializedName("profileData") var profileData: APIProfileDataPOJO? = APIProfileDataPOJO()

)