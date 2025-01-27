package com.clevertap.demo.ecom.productExperiences

import com.google.gson.annotations.SerializedName


data class TopCategoriesPOJO (

  @SerializedName("top_categories" ) var topCategories : ArrayList<CategoryPOJO> = arrayListOf()

)