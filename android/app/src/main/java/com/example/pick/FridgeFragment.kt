package com.example.pick

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pick.databinding.FragmentFridgeBinding

class FridgeFragment : Fragment() {

    private var _binding: FragmentFridgeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFridgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spanCount = calSpanCount()
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)

        val foodList = listOf(
            FoodItem("우유", R.drawable.ic_home),
            FoodItem("계란", R.drawable.ic_myinfo),
            FoodItem("치즈", R.drawable.ic_placeholder),
            FoodItem("우유", R.drawable.ic_placeholder),
            FoodItem("계란", R.drawable.ic_placeholder),
            FoodItem("치즈", R.drawable.ic_placeholder)
        )

        adapter = FoodAdapter(foodList, onItemClick = { foodItem ->
            val fragment = FoodInfoFragment.newInstance(foodItem.name, foodItem.imageResId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }, enableSelection = false) // 식재료 선택 불가능
        binding.recyclerView.adapter = adapter

        // 뒤로 가기 버튼 클릭 시 Fragment 스택에서 제거
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 열 개수 계산
    private fun calSpanCount(): Int {
        val screenWidth = resources.displayMetrics.widthPixels
        val itemWidth = resources.getDimension(R.dimen.food_image_size) +
                resources.getDimension(R.dimen.food_item_padding) * 2
        return (screenWidth / itemWidth).toInt().coerceAtLeast(2) // 최소 2열 유지
    }
}
