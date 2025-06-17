package com.example.pickandcook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.api.SavedRecipeResponse
import com.example.pickandcook.databinding.FragmentSavedRecipeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavedRecipeFragment : Fragment() {

    private var _binding: FragmentSavedRecipeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSavedRecipes()
    }

    private fun loadSavedRecipes() {
//        // 테스트용 하드코딩 데이터
//        val testList = listOf(
//            SavedRecipeResponse(recipeNo = 1, ckgNm = "된장찌개"),
//            SavedRecipeResponse(recipeNo = 2, ckgNm = "김치볶음밥"),
//            SavedRecipeResponse(recipeNo = 3, ckgNm = "계란말이"),
//            SavedRecipeResponse(recipeNo = 4, ckgNm = "떡볶이")
//        )
//
//        val adapter = SavedRecipeAdapter(testList) { item: SavedRecipeResponse ->
//            val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
//            intent.putExtra("recipeNo", item.recipeNo)
//            startActivity(intent)
//        }
//
//        binding.recipeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
//        binding.recipeRecyclerView.adapter = adapter
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getSavedRecipes(userId)
            .enqueue(object : Callback<List<SavedRecipeResponse>> {
                override fun onResponse(
                    call: Call<List<SavedRecipeResponse>>,
                    response: Response<List<SavedRecipeResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val savedList = response.body()!!

                        val adapter = SavedRecipeAdapter(savedList) { item: SavedRecipeResponse  ->
                            val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
                            intent.putExtra("recipeNo", item.recipeNo)
                            startActivity(intent)
                        }

                        binding.recipeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                        binding.recipeRecyclerView.adapter = adapter
                    } else {
                        Toast.makeText(requireContext(), "레시피 불러오기 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<SavedRecipeResponse>>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
