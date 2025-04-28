package com.example.pickandcook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class RecipeRecommendation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("RECOMMEND", "🔥 RecipeRecommendation 액티비티 시작됨")
        super.onCreate(savedInstanceState)

        val selectedFridgeItems = IngredientStore.selectedIngredients.map { it.fridgeIngredient }
        val selectedCartItems = IngredientStore.selectedShoppingItems.map { it.name }
        val selectedIngredients = selectedFridgeItems + selectedCartItems

        val selectedKind = intent.getStringExtra("category_kind")
        val selectedSituation = intent.getStringExtra("category_situation")
        val selectedMethod = intent.getStringExtra("category_method")

        Log.d("RECOMMEND", "✅ 냉장고 식재료: $selectedFridgeItems")
        Log.d("RECOMMEND", "✅ 쇼핑카트 식재료: $selectedCartItems")
        Log.d("RECOMMEND", "✅ 전체 사용 식재료: $selectedIngredients")
        Log.d("RECOMMEND", "✅ 선택된 카테고리 - 종류: $selectedKind, 상황: $selectedSituation, 방법: $selectedMethod")

        if (selectedIngredients.isEmpty() && selectedKind == null && selectedSituation == null && selectedMethod == null) {
            Toast.makeText(this, "선택된 조건이 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        thread {
            val recipeList = MySQLHelper.getFilteredRecipes(
                selectedKind, selectedSituation, selectedMethod, selectedIngredients
            )

            Log.d("RECOMMEND", "✅ 필터링된 레시피 수: ${recipeList.size}")
            Log.d("RECOMMEND", "✅ 추천 레시피: $recipeList")

            runOnUiThread {
                if (recipeList.isEmpty()) {
                    Toast.makeText(this, "조건에 맞는 레시피가 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                    return@runOnUiThread
                }

                val intent = Intent(this, RecipeResultActivity::class.java)
                intent.putParcelableArrayListExtra("recipes", ArrayList(recipeList))
                startActivity(intent)
                finish()
            }
        }
    }
}
