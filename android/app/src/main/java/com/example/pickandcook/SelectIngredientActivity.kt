package com.example.pickandcook

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pickandcook.databinding.ActivitySelectIngredientBinding

class SelectIngredientActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectIngredientBinding
    private lateinit var fridgeAdapter: FoodAdapter
    private lateinit var cartAdapter: ShoppingAdapter

    private val fridgeItems = listOf(
        FoodItem("우유", R.drawable.ic_home),
        FoodItem("계란", R.drawable.ic_myinfo),
        FoodItem("치즈", R.drawable.ic_placeholder),
        FoodItem("토마토", R.drawable.ic_placeholder),
        FoodItem("우유", R.drawable.ic_home),
        FoodItem("계란", R.drawable.ic_myinfo),
        FoodItem("치즈", R.drawable.ic_placeholder),
        FoodItem("토마토", R.drawable.ic_placeholder),
        FoodItem("우유", R.drawable.ic_home),
        FoodItem("계란", R.drawable.ic_myinfo),
        FoodItem("치즈", R.drawable.ic_placeholder),
        FoodItem("토마토", R.drawable.ic_placeholder)
    )

    private val cartItems = mutableListOf(
        ShoppingItem("우유", true),
        ShoppingItem("사과", false),
        ShoppingItem("양배추", true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectIngredientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // 냉장고 리스트
        fridgeAdapter = FoodAdapter(fridgeItems, onItemClick = { item ->
            Toast.makeText(this, "${item.name} 선택됨", Toast.LENGTH_SHORT).show()

        }, enableSelection = true) // 식재료 선택 가능
        binding.fridgeRecyclerView.adapter = fridgeAdapter
        binding.fridgeRecyclerView.post {
            val spanCount = calSpanCount(binding.fridgeRecyclerView.width)
            binding.fridgeRecyclerView.layoutManager = GridLayoutManager(this, spanCount)
        }

        // 쇼핑 카트 리스트
        cartAdapter = ShoppingAdapter(
            cartItems,
            onDelete = {},
            enableSelection = true,
            enableDelete = false
        )
        binding.cartRecyclerView.adapter = cartAdapter
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)

        // 다음 버튼
        binding.nextButton.setOnClickListener {
            // 전역 저장소에 선택 식재료 저장
            IngredientStore.selectedIngredients = fridgeAdapter.getSelectedItems()
            IngredientStore.selectedShoppingItems = cartAdapter.getSelectedItems()

            val isFridgeEmpty = IngredientStore.selectedIngredients.isEmpty()
            val isCartEmpty = IngredientStore.selectedShoppingItems.isEmpty()

            if (isFridgeEmpty && isCartEmpty) {
                AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("식재료를 하나 이상 선택해주세요.")
                    .setPositiveButton("확인", null)
                    .show()
                return@setOnClickListener
            }

            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }
    }

    // 열 개수 계산
    private fun calSpanCount(viewWidth: Int): Int {
        val itemWidth = resources.getDimension(R.dimen.food_image_size) +
                resources.getDimension(R.dimen.food_item_padding) * 2
        return (viewWidth / itemWidth).toInt().coerceAtLeast(2) // 최소 2열
    }
}
