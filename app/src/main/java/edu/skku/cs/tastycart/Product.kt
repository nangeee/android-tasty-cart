package edu.skku.cs.tastycart

data class Product(
    val productName: String = "",
    val originalPrice: Double = 0.0,
    val quantity: String = "",
    val discount: String? = "",
    val discountedPrice: Double? = null,
    val category: String? = null
)
