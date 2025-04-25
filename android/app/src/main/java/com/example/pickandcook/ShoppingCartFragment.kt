package com.example.pickandcook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.api.BarcodeResponse
import com.example.pickandcook.api.CartRequest
import com.example.pickandcook.api.CartResponse
import com.example.pickandcook.databinding.FragmentShoppingCartBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShoppingCartFragment : Fragment() {

    private var _binding: FragmentShoppingCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ShoppingAdapter

    // 중복 기준 식재료 리스트 (하드코딩)
    private val duplicatedItems = listOf("우유", "양배추")

    // 쇼핑카트 아이템 리스트 (하드코딩)
    private val itemNames = listOf("우유", "양배추", "사과", "당근")
    private val itemList = itemNames.map { name ->
        ShoppingItem(name, duplicatedItems.contains(name))
    }.toMutableList()

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

        // RecyclerView + 삭제 기능
        adapter = ShoppingAdapter(itemList, onDelete = { itemToRemove ->
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("삭제하시겠습니까?")
                .setMessage("\"${itemToRemove.name}\"(이)가 삭제됩니다.")
                .setPositiveButton("확인") { _, _ ->
                    adapter.removeItem(itemToRemove)
                }
                .setNegativeButton("취소", null)
                .show()
        }, enableSelection = false, enableDelete = true)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // 바코드 스캔 버튼 클릭
        binding.cameraButton.setOnClickListener {
            startBarcodeScanner()
        }

        // 뒤로 가기
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fetchCartItems()
    }

    private fun fetchCartItems() {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val call = RetrofitClient.instance.getCartItems(userId)
        call.enqueue(object : Callback<List<BarcodeResponse>> {
            override fun onResponse(
                call: Call<List<BarcodeResponse>>,
                response: Response<List<BarcodeResponse>>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(requireContext(), "장바구니 조회 실패", Toast.LENGTH_SHORT).show()
                    return
                }
                val cartItems = response.body()
                if (cartItems.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "장바구니가 비어 있습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 가격 표시 제거됨
                    val cartInfo = cartItems.joinToString("\n") { "상품명: ${it.ingredientName}" }
                    Toast.makeText(requireContext(), cartInfo, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<BarcodeResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류 발생: ${t.message}", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
