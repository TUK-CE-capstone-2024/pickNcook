package com.example.pickandcook

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pickandcook.api.BarcodeResponse
import com.example.pickandcook.api.FridgeItem
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.databinding.FragmentSelectIngredientBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectIngredientFragment : Fragment() {

    private var _binding: FragmentSelectIngredientBinding? = null
    private val binding get() = _binding!!

    private lateinit var fridgeAdapter: FridgeAdapter
    private lateinit var shoppingAdapter: ShoppingAdapter

    private var fridgeIngredients: List<String> = emptyList()

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentSelectIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        fetchFridgeItems()

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.nextButton.setOnClickListener {
            val selectedFridgeItems = fridgeAdapter.getSelectedItems()
            val selectedCartItems = shoppingAdapter.getSelectedItems()

            if (selectedFridgeItems.isEmpty() && selectedCartItems.isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("알림")
                    .setMessage("식재료를 하나 이상 선택해주세요.")
                    .setPositiveButton("확인", null)
                    .show()
            } else {
                // 전역 저장소에 선택 식재료 저장
                IngredientStore.selectedIngredients = selectedFridgeItems
                IngredientStore.selectedShoppingItems = selectedCartItems

                parentFragmentManager.commit {
                    replace(R.id.mainFragmentContainer, CategoryFragment())
                    addToBackStack(null)
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        binding.fridgeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        fridgeAdapter = FridgeAdapter(mutableListOf(), enableSelection = true)
        binding.fridgeRecyclerView.adapter = fridgeAdapter

        binding.cartRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        shoppingAdapter = ShoppingAdapter(mutableListOf(), {}, enableSelection = true, enableDelete = false)
        binding.cartRecyclerView.adapter = shoppingAdapter
    }

    private fun fetchFridgeItems() {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getFridgeItems(userId).enqueue(object : Callback<List<FridgeItem>> {
            override fun onResponse(call: Call<List<FridgeItem>>, response: Response<List<FridgeItem>>) {
                if (response.isSuccessful) {
                    val items = response.body()?.map { item ->
                        item.copy(imageResId = getIngredientImage(item.fridgeIngredient))
                    } ?: emptyList()

                    fridgeIngredients = items.map { it.fridgeIngredient }
                    fridgeAdapter.updateItems(items)
                    fetchCartItems()
                } else {
                    Toast.makeText(requireContext(), "냉장고 식재료 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FridgeItem>>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchCartItems() {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getCartItems(userId).enqueue(object : Callback<List<BarcodeResponse>> {
            override fun onResponse(call: Call<List<BarcodeResponse>>, response: Response<List<BarcodeResponse>>) {
                if (response.isSuccessful) {
                    val cartItems = response.body()?.map { barcodeItem ->
                        ShoppingItem(
                            name = barcodeItem.ingredientName,
                            barcode = barcodeItem.barcodeNum,
                            showWarning = fridgeIngredients.contains(barcodeItem.ingredientName)
                        )
                    } ?: emptyList()

                    shoppingAdapter.updateItems(cartItems)
                } else {
                    Toast.makeText(requireContext(), "쇼핑카트 식재료 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<BarcodeResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getIngredientImage(ingredientName: String): Int {
        return when (ingredientName) {
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
            else -> R.drawable.ic_placeholder
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
