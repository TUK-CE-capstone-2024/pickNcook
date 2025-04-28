package com.example.pickandcook

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pickandcook.api.BarcodeResponse
import com.example.pickandcook.api.CartRequest
import com.example.pickandcook.api.CartResponse
import com.example.pickandcook.api.FridgeItem
import com.example.pickandcook.api.RetrofitClient
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShoppingCartFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ShoppingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_shopping_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = ShoppingAdapter(mutableListOf(), { item ->
            deleteCartItem(item)
        }, enableDelete = true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val cameraButton: ImageView = view.findViewById(R.id.cameraButton)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        cameraButton.setOnClickListener {
            startBarcodeScanner()
        }

        fetchCartAndFridgeItems()
    }

    private fun fetchCartAndFridgeItems() {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null) ?: return

        RetrofitClient.instance.getFridgeItems(userId).enqueue(object : Callback<List<FridgeItem>> {
            override fun onResponse(call: Call<List<FridgeItem>>, response: Response<List<FridgeItem>>) {
                if (response.isSuccessful) {
                    val fridgeIngredients = response.body()?.map { it.fridgeIngredient } ?: emptyList()

                    RetrofitClient.instance.getCartItems(userId).enqueue(object : Callback<List<BarcodeResponse>> {
                        override fun onResponse(call: Call<List<BarcodeResponse>>, response: Response<List<BarcodeResponse>>) {
                            if (response.isSuccessful) {
                                val cartItems = response.body()?.map { barcodeItem ->
                                    ShoppingItem(
                                        name = barcodeItem.ingredientName,
                                        showWarning = fridgeIngredients.contains(barcodeItem.ingredientName)
                                    )
                                } ?: emptyList()

                                adapter.updateItems(cartItems)
                            } else {
                                Toast.makeText(requireContext(), "장바구니 조회 실패", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<List<BarcodeResponse>>, t: Throwable) {
                            Toast.makeText(requireContext(), "장바구니 네트워크 오류", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(requireContext(), "냉장고 조회 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FridgeItem>>, t: Throwable) {
                Toast.makeText(requireContext(), "냉장고 네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteCartItem(item: BarcodeResponse) { /* 기존 삭제코드 유지 */ }
    private fun startBarcodeScanner() { /* 스캐너 유지 */ }
    private fun fetchProductInfo(barcode: String) { /* 상품 정보 유지 */ }
    private fun addBarcodeToCart(barcode: String) { /* 장바구니 추가 유지 */ }
}
