package com.example.pickandcook



// 전역 저장소
object IngredientStore {
    var selectedIngredients: List<FoodItem> = emptyList()
    var selectedShoppingItems: List<ShoppingItem> = emptyList()
}