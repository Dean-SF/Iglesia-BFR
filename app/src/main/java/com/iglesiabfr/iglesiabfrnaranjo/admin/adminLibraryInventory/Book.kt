package com.iglesiabfr.iglesiabfrnaranjo.admin.events

data class Book(
    val title: String,
    val name: String,
    val quantity: Int,
    val price: Double
) {
    init {
        require(quantity >= 0) { "Quantity must be non-negative" }
        require(price >= 0.0) { "Price must be non-negative" }
    }
}
