package com.example.pickandcook

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pickandcook.api.FavoriteRequest
import com.example.pickandcook.api.PreferIngredient
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.databinding.FragmentPreferFoodBinding
import okhttp3.ResponseBody

class PreferFoodFragment : Fragment() {

    private var _binding: FragmentPreferFoodBinding? = null
    private val binding get() = _binding!!

    private val foodList = mutableListOf<FoodItem>()
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreferFoodBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 한글 입력 확인용 로그
        binding.etFoodName.addTextChangedListener {
            android.util.Log.d("한글입력", "현재 입력: ${it.toString()}")
        }

        /*
        binding.btnAddFood.setOnClickListener {
            val foodName = binding.etFoodName.text.toString().trim()
            if (foodName.isNotEmpty()) {
                addFoodItem(foodName)
                binding.etFoodName.text?.clear()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("주의")
                    .setMessage("식품 이름을 입력하세요.")
                    .setPositiveButton("확인", null)
                    .show()
            }
        }

         */


        binding.btnAddFood.setOnClickListener {
            val foodName = binding.etFoodName.text.toString().trim()

            if (foodName.isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("주의")
                    .setMessage("식품 이름을 입력하세요.")
                    .setPositiveButton("확인", null)
                    .show()
                return@setOnClickListener
            }

            val userId = requireContext()
                .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("userId", null)

            if (userId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = FavoriteRequest(userId, foodName)

            RetrofitClient.instance.addFavorite(request)
                .enqueue(object : retrofit2.Callback<Map<String, String>> {
                    override fun onResponse(
                        call: retrofit2.Call<Map<String, String>>,
                        response: retrofit2.Response<Map<String, String>>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "선호 식재료가 저장되었습니다", Toast.LENGTH_SHORT).show()
                            addFoodItem(foodName) // 성공한 경우에만 리스트에 추가
                            binding.etFoodName.text?.clear()
                        } else {
                            Toast.makeText(requireContext(), "저장 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(
                        call: retrofit2.Call<Map<String, String>>,
                        t: Throwable
                    ) {
                        Toast.makeText(requireContext(), "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }




        fetchUserFavoriteIngredients()

    }



    private fun setupRecyclerView() {
        adapter = FoodAdapter(
            items = foodList,
            onItemClick = {},
            onDeleteClick = { item -> confirmDelete(item) },
            showDeleteButton = true,
            enableSelection = false
        )
        val spanCount = 3
        binding.rvFavoriteFoods.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.rvFavoriteFoods.adapter = adapter
    }

    /*
    private fun confirmDelete(item: FoodItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("삭제 확인")
            .setMessage("‘${item.name}’을(를) 삭제하시겠어요?")
            .setPositiveButton("삭제") { _, _ ->
                foodList.remove(item)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("취소", null)
            .show()
    }

     */

    private fun confirmDelete(item: FoodItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("삭제 확인")
            .setMessage("‘${item.name}’을(를) 삭제하시겠어요?")
            .setPositiveButton("삭제") { _, _ ->

                val userId = requireContext()
                    .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    .getString("userId", null)

                if (userId.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                RetrofitClient.instance.deleteFavorite(userId, item.name)
                    .enqueue(object : retrofit2.Callback<Map<String, String>> {
                        override fun onResponse(
                            call: retrofit2.Call<Map<String, String>>,
                            response: retrofit2.Response<Map<String, String>>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(requireContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show()
                                foodList.remove(item)
                                adapter.notifyDataSetChanged()
                            } else {
                                Toast.makeText(requireContext(), "삭제 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(
                            call: retrofit2.Call<Map<String, String>>,
                            t: Throwable
                        ) {
                            Toast.makeText(requireContext(), "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("취소", null)
            .show()
    }



    private fun addFoodItem(name: String) {
        val resId = getImageResourceForIngredient(name)
        foodList.add(FoodItem(name, resId))
        adapter.notifyItemInserted(foodList.size - 1)
    }

    private fun fetchUserFavoriteIngredients() {
        val userId = requireContext()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("userId", null)

        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getPreferIngredients(userId)
            .enqueue(object : retrofit2.Callback<List<PreferIngredient>> {
                override fun onResponse(
                    call: retrofit2.Call<List<PreferIngredient>>,
                    response: retrofit2.Response<List<PreferIngredient>>
                ) {
                    if (response.isSuccessful) {
                        val ingredients = response.body() ?: emptyList()
                        foodList.clear()
                        foodList.addAll(
                            ingredients.map {
                                val name = it.preferIngredient
                                val resId = getImageResourceForIngredient(name)
                                FoodItem(name, resId)
                            }
                        )
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "불러오기 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<List<PreferIngredient>>,
                    t: Throwable
                ) {
                    Toast.makeText(requireContext(), "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }



    private fun getImageResourceForIngredient(name: String): Int {
        return when (name) {
            "마늘" -> R.drawable.ic_garlic
            "감자" -> R.drawable.ic_potato
            "달걀" -> R.drawable.ic_egg
            "양파" -> R.drawable.ic_onion
            "닭고기" -> R.drawable.ic_chicken
            "돼지고기" -> R.drawable.ic_pork
            "소고기" -> R.drawable.ic_beef
            "대파" -> R.drawable.ic_greenonion
            "김치" -> R.drawable.ic_kimchi
            "햄" -> R.drawable.ic_ham
            "콩나물" -> R.drawable.ic_beansprouts
            else -> R.drawable.ic_grocery
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
