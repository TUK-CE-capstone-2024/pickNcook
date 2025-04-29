package com.example.pickandcook

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import kotlin.concurrent.thread

class RecipeRecommendationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 별도 XML 없이 빈 View 리턴
        return View(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("RECOMMEND", "🔥 RecipeRecommendationFragment 시작됨")
        super.onViewCreated(view, savedInstanceState)

        val selectedFridgeItems = IngredientStore.selectedIngredients.map { it.fridgeIngredient }
        val selectedCartItems = IngredientStore.selectedShoppingItems.map { it.name }
        val selectedIngredients = selectedFridgeItems + selectedCartItems

        val selectedKind = arguments?.getString("category_kind")
        val selectedSituation = arguments?.getString("category_situation")
        val selectedMethod = arguments?.getString("category_method")

        Log.d("RECOMMEND", "✅ 냉장고 식재료: $selectedFridgeItems")
        Log.d("RECOMMEND", "✅ 쇼핑카트 식재료: $selectedCartItems")
        Log.d("RECOMMEND", "✅ 전체 사용 식재료: $selectedIngredients")
        Log.d("RECOMMEND", "✅ 선택된 카테고리 - 종류: $selectedKind, 상황: $selectedSituation, 방법: $selectedMethod")

        if (selectedIngredients.isEmpty() && selectedKind == null && selectedSituation == null && selectedMethod == null) {
            Toast.makeText(requireContext(), "선택된 조건이 없습니다.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        thread {
            val recipeList = MySQLHelper.getFilteredRecipes(
                selectedKind, selectedSituation, selectedMethod, selectedIngredients
            )

            Log.d("RECOMMEND", "✅ 필터링된 레시피 수: ${recipeList.size}")
            Log.d("RECOMMEND", "✅ 추천 레시피: $recipeList")

            requireActivity().runOnUiThread {
                if (recipeList.isEmpty()) {
                    Toast.makeText(requireContext(), "조건에 맞는 레시피가 없습니다.", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                    return@runOnUiThread
                }

                val fragment = RecipeResultFragment().apply {
                    arguments = Bundle().apply {
                        putParcelableArrayList("recipes", ArrayList(recipeList))
                    }
                }

                parentFragmentManager.commit {
                    replace(R.id.mainFragmentContainer, fragment)
                    addToBackStack(null)
                }
            }
        }
    }
}
