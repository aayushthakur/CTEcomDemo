package com.clevertap.demo.ecom.api

import com.google.gson.annotations.SerializedName


data class APIResultPOJO (

  @SerializedName("record" ) var record : APIRecordPOJO? = APIRecordPOJO(),
  @SerializedName("status" ) var status : String? = null

)