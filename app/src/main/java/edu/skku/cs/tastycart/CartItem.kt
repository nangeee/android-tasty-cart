package edu.skku.cs.tastycart

data class CartItem(
    val productName: String = "",
    val productPrice: Double = 0.0,
    var quantity: Int = 0
)
