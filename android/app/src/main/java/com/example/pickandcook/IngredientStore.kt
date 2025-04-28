package com.example.pickandcook

import com.example.pickandcook.api.FridgeItem


// 전역 저장소
object IngredientStore {
    var selectedIngredients: List<FridgeItem> = emptyList()
    var selectedShoppingItems: List<ShoppingItem> = emptyList()
}