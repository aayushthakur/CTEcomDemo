package com.clevertap.demo.ecom.mainFragments

data class Product(
    val id: String,
    val title: String,
    val price: Int,
    val original_price: Int,
    val discount: String,
    val color: String,
    val image_url: String
)