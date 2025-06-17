package com.example.pickandcook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pickandcook.databinding.FragmentPreferFoodBinding

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

    private fun addFoodItem(name: String) {
        val resId = getImageResourceForIngredient(name)
        foodList.add(FoodItem(name, resId))
        adapter.notifyItemInserted(foodList.size - 1)
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
