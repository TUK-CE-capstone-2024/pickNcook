package com.example.pickandcook

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.pickandcook.api.RecipeFilterRequest
import com.example.pickandcook.api.RecipeItem
import com.example.pickandcook.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeRecommendationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("RECOMMEND", "🔥 RecipeRecommendationFragment 시작됨")
        super.onViewCreated(view, savedInstanceState)

        val selectedFridgeItems = IngredientStore.selectedIngredients.map { it.fridgeIngredient }
        val selectedCartItems = IngredientStore.selectedShoppingItems.map { it.name }
        val selectedIngredients = selectedFridgeItems + selectedCartItems

        val selectedKind = arguments?.getString("category_kind") ?: ""
        val selectedSituation = arguments?.getString("category_situation") ?: ""
        val selectedMethod = arguments?.getString("category_method") ?: ""

        Log.d("RECOMMEND", "✅ 냉장고 식재료: $selectedFridgeItems")
        Log.d("RECOMMEND", "✅ 쇼핑카트 식재료: $selectedCartItems")
        Log.d("RECOMMEND", "✅ 전체 사용 식재료: $selectedIngredients")
        Log.d("RECOMMEND", "✅ 선택된 카테고리 - 종류: $selectedKind, 상황: $selectedSituation, 방법: $selectedMethod")

        if (selectedIngredients.isEmpty() && selectedKind.isEmpty() && selectedSituation.isEmpty() && selectedMethod.isEmpty()) {
            Toast.makeText(requireContext(), "선택된 조건이 없습니다.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = RecipeFilterRequest(
                    kind = if (selectedKind.isBlank()) null else selectedKind,
                    situation = if (selectedSituation.isBlank()) null else selectedSituation,
                    method = if (selectedMethod.isBlank()) null else selectedMethod,
                    ingredients = if (selectedIngredients.isEmpty()) null else selectedIngredients
                )

                val response = RetrofitClient.instance.filterRecipes(request).execute()
                val recipeList = response.body() ?: emptyList()

                withContext(Dispatchers.Main) {
                    if (recipeList.isEmpty()) {
                        Toast.makeText(requireContext(), "조건에 맞는 레시피가 없습니다.", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                        return@withContext
                    }

                    val recipeItemList = recipeList.map {
                        RecipeItem(recipeNo = it.recipeNo, recipeName = it.rcpTtl)
                    }

                    val fragment = RecipeResultFragment().apply {
                        arguments = Bundle().apply {
                            putParcelableArrayList("recipes", ArrayList(recipeItemList))
                        }
                    }

                    parentFragmentManager.commit {
                        hide(this@RecipeRecommendationFragment)
                        add(R.id.mainFragmentContainer, fragment)
                        addToBackStack(null)
                    }
                }
            } catch (e: Exception) {
                Log.e("RECOMMEND", "API 호출 실패: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "서버 오류 발생", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }

        /*
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.filterRecipes(
                    selectedKind, selectedSituation, selectedMethod, selectedIngredients
                ).execute()

                val recipeList = response.body() ?: emptyList()

                withContext(Dispatchers.Main) {
                    if (recipeList.isEmpty()) {
                        Toast.makeText(requireContext(), "조건에 맞는 레시피가 없습니다.", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                        return@withContext
                    }

                    val recipeItemList = recipeList.map {
                        RecipeItem(recipeNo = it.recipeNo, recipeName = it.rcpTtl)
                    }

                    val fragment = RecipeResultFragment().apply {
                        arguments = Bundle().apply {
                            putParcelableArrayList("recipes", ArrayList(recipeItemList))
                        }
                    }


                    parentFragmentManager.commit {
                        hide(this@RecipeRecommendationFragment)
                        add(R.id.mainFragmentContainer, fragment)
                        addToBackStack(null)
                    }
                }
            } catch (e: Exception) {
                Log.e("RECOMMEND", "API 호출 실패: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "서버 오류 발생", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }*/
    }
}
