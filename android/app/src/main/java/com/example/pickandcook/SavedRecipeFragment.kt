package com.example.pickandcook

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pickandcook.api.RetrofitClient
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
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getSavedRecipes(userId)
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if (response.isSuccessful) {
                        val recipes = response.body() ?: emptyList()

                        binding.recipeContainer.removeAllViews()

                        for (recipeName in recipes) {
                            val textView = TextView(requireContext()).apply {
                                text = recipeName
                                textSize = 18f
                                setTypeface(null, Typeface.BOLD)
                                setTextColor(Color.BLACK)
                                setPadding(40, 60, 40, 60)
                                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded)
                                layoutParams = LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    setMargins(12, 20, 12, 20)
                                }
                                gravity = android.view.Gravity.CENTER

                                setOnClickListener {
                                    val intent = Intent(requireContext(), RecipeDetailActivity::class.java)
                                    intent.putExtra("recipeName", recipeName) // 레시피 이름 넘기기
                                    startActivity(intent)
                                }
                            }
                            binding.recipeContainer.addView(textView)
                        }
                    } else {
                        Toast.makeText(requireContext(), "불러오기 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
