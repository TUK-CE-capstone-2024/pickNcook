package com.example.pickandcook

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pickandcook.api.BarcodeResponse
import com.example.pickandcook.api.CartRequest
import com.example.pickandcook.api.CartResponse
import com.example.pickandcook.api.FridgeItem
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.databinding.FragmentShoppingCartBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import androidx.activity.result.contract.ActivityResultContracts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShoppingCartFragment : Fragment() {

    private var _binding: FragmentShoppingCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ShoppingAdapter

    private val barcodeScannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val intentResult: IntentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
        if (intentResult.contents != null) {
            fetchProductInfo(intentResult.contents)
        } else {
            Toast.makeText(requireContext(), "스캔 취소", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ShoppingAdapter(mutableListOf(), { item ->
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("삭제하시겠습니까?")
                .setMessage("\"${item.name}\"(이)가 삭제됩니다.")
                .setPositiveButton("확인") { _, _ ->
                    deleteCartItem(item) // 확인 시에만 삭제 실행
                }
                .setNegativeButton("취소", null)
                .show()
        }, enableDelete = true)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.cameraButton.setOnClickListener {
            startBarcodeScanner()
        }

        fetchCartItems()
    }

    private fun fetchCartItems() {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null) ?: return

        RetrofitClient.instance.getFridgeItems(userId).enqueue(object : Callback<List<FridgeItem>> {
            override fun onResponse(call: Call<List<FridgeItem>>, response: Response<List<FridgeItem>>) {
                if (response.isSuccessful) {
                    val cartIngredients = response.body()?.map { it.fridgeIngredient } ?: emptyList()

                    RetrofitClient.instance.getCartItems(userId).enqueue(object : Callback<List<BarcodeResponse>> {
                        override fun onResponse(call: Call<List<BarcodeResponse>>, response: Response<List<BarcodeResponse>>) {
                            if (response.isSuccessful) {
                                val cartItems = response.body()?.map { barcodeItem ->
                                    ShoppingItem(
                                        name = barcodeItem.ingredientName,
                                        barcode = barcodeItem.barcodeNum,
                                        price = barcodeItem.price, // ✅ 가격 정보 사용
                                        showWarning = cartIngredients.contains(barcodeItem.ingredientName)
                                    )
                                } ?: emptyList()

                                adapter.updateItems(cartItems)

                                // ✅ 총합 계산
                                val totalPrice = cartItems.sumOf { it.price }
                                binding.totalPriceText.text = "총합: ${totalPrice}원"
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

        private fun deleteCartItem(item: ShoppingItem) {
            val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userId = sharedPref.getString("userId", null) ?: return

            RetrofitClient.instance.deleteCartItem(userId, item.barcode).enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        adapter.removeItem(item) // 삭제 성공 시 화면에서도 삭제
                    } else {
                        Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        private fun startBarcodeScanner() {
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            integrator.setPrompt("바코드를 스캔하세요")
            integrator.setBeepEnabled(false)
            integrator.setBarcodeImageEnabled(true)
            integrator.setOrientationLocked(false)
            barcodeScannerLauncher.launch(integrator.createScanIntent())
        }
        private fun fetchProductInfo(barcode: String) {
            val call = RetrofitClient.instance.getProduct(barcode)
            call.enqueue(object : Callback<BarcodeResponse> {
                override fun onResponse(call: Call<BarcodeResponse>, response: Response<BarcodeResponse>) {
                    if (response.isSuccessful) {
                        val product = response.body()
                        product?.let {
                            Toast.makeText(
                                requireContext(),
                                "상품명: ${it.ingredientName}", // 가격 제거
                                Toast.LENGTH_LONG
                            ).show()
                            addBarcodeToCart(barcode)
                        }
                    } else {
                        Toast.makeText(requireContext(), "상품 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BarcodeResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                }
            })
        }
        private fun addBarcodeToCart(barcode: String) {
            val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userId = sharedPref.getString("userId", null)

            if (userId == null) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return
            }

            val request = CartRequest(userId, barcode)
            val call = RetrofitClient.instance.addToCart(request)
            call.enqueue(object : Callback<CartResponse> {
                override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                    if (response.isSuccessful) {
                        val cartResponse = response.body()
                        cartResponse?.message?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                            fetchCartItems()
                        }
                    } else {
                        Toast.makeText(requireContext(), "장바구니 추가 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류 발생: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
