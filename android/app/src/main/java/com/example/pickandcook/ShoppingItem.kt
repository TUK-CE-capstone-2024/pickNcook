package com.example.pickandcook

data class ShoppingItem(
    val name: String,            // 식재료 이름
    val barcode: String,          // 바코드 번호
    val showWarning: Boolean
)
