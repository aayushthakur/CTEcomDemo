package com.clevertap.demo.ecom.mainFragments

object CartManager {
    private val cartItems = mutableListOf<Product>()

    fun addToCart(product: Product) {
        if (!cartItems.any { it.id == product.id }) {
            cartItems.add(product)
        }
    }

    fun removeFromCart(product: Product) {
        cartItems.removeAll { it.id == product.id }
    }

    fun getCartItems(): List<Product> {
        return cartItems.toList()
    }

    fun isProductInCart(productId: String): Boolean {
        return cartItems.any { it.id == productId }
    }
}