package com.example.pickandcook

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pickandcook.api.FridgeItem
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.databinding.FragmentFridgeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class FridgeFragment : Fragment() {

    private var _binding: FragmentFridgeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FoodAdapter
    private var serverIpAddress: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFridgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchServerIp()

        val spanCount = calSpanCount()
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)

        loadFoodList()

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.addButton.setOnClickListener {
            runYoloAndRefresh()
        }

        fetchFridgeItems()
    }

    private fun fetchServerIp() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://172.30.1.98:5000/get-server-ip")
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("FridgeFragment", "서버 IP 응답: $responseBody")

                    val ipRegex = """"server_ip"\s*:\s*"([^"]+)""".toRegex()
                    val matchResult = ipRegex.find(responseBody ?: "")

                    matchResult?.groups?.get(1)?.value?.let { ip ->
                        serverIpAddress = ip
                        Log.d("FridgeFragment", "Flask 서버 IP 저장 완료: $serverIpAddress")
                    }
                } else {
                    Log.e("FridgeFragment", "서버 IP 가져오기 실패: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("FridgeFragment", "서버 IP 가져오는 중 오류", e)
            }
        }
    }

    private fun loadFoodList() {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getFridgeItems(userId).enqueue(object : Callback<List<FridgeItem>> {
            override fun onResponse(call: Call<List<FridgeItem>>, response: Response<List<FridgeItem>>) {
                if (response.isSuccessful) {
                    val fridgeItems = response.body()?: emptyList()

                    val foodItems = fridgeItems.map { fridgeItem ->
                        FoodItem(
                            name = fridgeItem.fridgeIngredient,
                            imageResId = getImageResourceForIngredient(fridgeItem.fridgeIngredient)
                        )
                    }

                    adapter = FoodAdapter(foodItems, onItemClick = { foodItem ->
                        Log.d("FridgeFragment", "사용자가 클릭한 식재료 이름: ${foodItem.name}")

                        val fragment = FoodInfoFragment.newInstance(foodItem.name, foodItem.imageResId)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.mainFragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit()
                    }, enableSelection = false)

                    binding.recyclerView.adapter = adapter

                } else {
                    Toast.makeText(requireContext(), "식재료 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FridgeItem>>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류 발생: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun calSpanCount(): Int {
        val screenWidth = resources.displayMetrics.widthPixels
        val itemMinWidth = 150 * resources.displayMetrics.density // 아이템 하나 최소 150dp
        return (screenWidth / itemMinWidth).toInt().coerceAtLeast(2)
    }

    private fun fetchFridgeItems() {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.getFridgeItems(userId).enqueue(object : Callback<List<FridgeItem>> {
            override fun onResponse(call: Call<List<FridgeItem>>, response: Response<List<FridgeItem>>) {
                if (response.isSuccessful) {
                    val fridgeItems = response.body() ?: emptyList()

                    val foodItems = fridgeItems.map { fridgeItem ->
                        FoodItem(
                            name = fridgeItem.fridgeIngredient,
                            imageResId = getImageResourceForIngredient(fridgeItem.fridgeIngredient)  // 수정
                        )
                    }

                    adapter = FoodAdapter(foodItems, onItemClick = { foodItem ->
                        Log.d("FridgeFragment", "사용자가 클릭한 식재료 이름: ${foodItem.name}")
                        val fragment = FoodInfoFragment.newInstance(foodItem.name, foodItem.imageResId)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.mainFragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit()
                    }, enableSelection = false)

                    binding.recyclerView.adapter = adapter

                } else {
                    Toast.makeText(requireContext(), "식재료 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FridgeItem>>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류 발생: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun runYoloAndRefresh() {
        CoroutineScope(Dispatchers.IO).launch {
            val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userId = sharedPref.getString("userId", null)

            if (serverIpAddress == null || userId == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "서버 연결 또는 로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()

                val requestBody = okhttp3.FormBody.Builder()
                    .add("userId", userId)
                    .build()

                val request = Request.Builder()
                    .url("$serverIpAddress/run-yolo")
                    .post(requestBody)
                    .build()

                val response: okhttp3.Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    Log.d("FridgeFragment", "YOLO 감지 성공")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "객체 인식 완료!", Toast.LENGTH_SHORT).show()
                        loadFoodList()
                    }
                } else {
                    Log.e("FridgeFragment", "YOLO 감지 실패: ${response.code}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "YOLO 감지 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("FridgeFragment", "YOLO 감지 중 오류 발생", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "오류 발생: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
            else -> R.drawable.ic_placeholder
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
