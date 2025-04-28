package com.example.pickandcook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pickandcook.api.BarcodeResponse
import com.example.pickandcook.api.FridgeItem
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.databinding.ActivitySelectIngredientBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectIngredientActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectIngredientBinding
    private lateinit var fridgeAdapter: FridgeAdapter
    private lateinit var shoppingAdapter: ShoppingAdapter

    private var fridgeIngredients: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectIngredientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        fetchFridgeItems()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.nextButton.setOnClickListener {

            val selectedFridgeItems = fridgeAdapter.getSelectedItems()
            val selectedCartItems = shoppingAdapter.getSelectedItems()

            if (selectedFridgeItems.isEmpty() && selectedCartItems.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("식재료를 하나 이상 선택해주세요.")
                    .setPositiveButton("확인", null)
                    .show()
            } else {
                IngredientStore.selectedIngredients = selectedFridgeItems
                IngredientStore.selectedShoppingItems = selectedCartItems

                val intent = Intent(this, CategoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerViews() {
        binding.fridgeRecyclerView.layoutManager = GridLayoutManager(this, 2)
        fridgeAdapter = FridgeAdapter(mutableListOf(), enableSelection = true)
        binding.fridgeRecyclerView.adapter = fridgeAdapter

        binding.cartRecyclerView.layoutManager = GridLayoutManager(this, 1)
        shoppingAdapter = ShoppingAdapter(mutableListOf(), {}, enableSelection = true, enableDelete = false)
        binding.cartRecyclerView.adapter = shoppingAdapter
    }

    private fun fetchFridgeItems() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getFridgeItems(userId).enqueue(object : Callback<List<FridgeItem>> {
            override fun onResponse(call: Call<List<FridgeItem>>, response: Response<List<FridgeItem>>) {
                if (response.isSuccessful) {
                    fridgeIngredients = response.body()?.map { it.fridgeIngredient } ?: emptyList()
                    fridgeAdapter.updateItems(response.body() ?: emptyList())
                    fetchCartItems()
                } else {
                    Toast.makeText(this@SelectIngredientActivity, "냉장고 식재료 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FridgeItem>>, t: Throwable) {
                Toast.makeText(this@SelectIngredientActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchCartItems() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getCartItems(userId).enqueue(object : Callback<List<BarcodeResponse>> {
            override fun onResponse(call: Call<List<BarcodeResponse>>, response: Response<List<BarcodeResponse>>) {
                if (response.isSuccessful) {
                    val cartItems = response.body()?.map { barcodeItem ->
                        ShoppingItem(
                            name = barcodeItem.ingredientName,
                            showWarning = fridgeIngredients.contains(barcodeItem.ingredientName)
                        )
                    } ?: emptyList()

                    shoppingAdapter.updateItems(cartItems)
                } else {
                    Toast.makeText(this@SelectIngredientActivity, "쇼핑카트 식재료 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<BarcodeResponse>>, t: Throwable) {
                Toast.makeText(this@SelectIngredientActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
