package com.example.pickandcook

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import com.example.pickandcook.databinding.FragmentFoodInfoBinding
import com.example.pickandcook.api.* // ChatGPT API 의존성
import kotlinx.coroutines.*
import retrofit2.*
import android.util.Base64
import androidx.appcompat.app.AlertDialog


class FoodInfoFragment : Fragment() {

    private var _binding: FragmentFoodInfoBinding? = null
    private val binding get() = _binding!!

    private var isFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString("name") ?: ""
        val imageResId = arguments?.getInt("imageResId") ?: R.drawable.ic_placeholder
        val userId = getUserIdFromSharedPrefs()
        checkFavoriteStatus(userId, name)


        binding.foodName.text = name
        binding.foodImage.setImageResource(imageResId)

        // 식재료 정보를 GPT API로 가져와서 표시 (달라진 부분 -> 식재료 정보들도 가져와야함)
        fetchIngredientInfo(name)


        /*
        // 하트 클릭 시 선호식품 표시
        binding.btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_heart_red
                else R.drawable.ic_heart_black
            )
        }
         */

        binding.btnFavorite.setOnClickListener {
            if (isFavorite) {
                // 삭제 요청
                RetrofitClient.instance.deleteFavorite(userId, name)
                    .enqueue(object : Callback<Map<String, String>> {
                        override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                            if (response.isSuccessful) {
                                isFavorite = false
                                binding.btnFavorite.setImageResource(R.drawable.ic_heart_black)
                            }
                        }

                        override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                            Log.e("FoodInfoFragment", "즐겨찾기 삭제 실패", t)
                        }
                    })
            } else {
                // 추가 요청
                val request = FavoriteRequest(userId, name)
                RetrofitClient.instance.addFavorite(request)
                    .enqueue(object : Callback<Map<String, String>> {
                        override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                            if (response.isSuccessful) {
                                isFavorite = true
                                binding.btnFavorite.setImageResource(R.drawable.ic_heart_red)
                            }
                        }

                        override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                            Log.e("FoodInfoFragment", "즐겨찾기 추가 실패", t)
                        }
                    })
            }
        }

        binding.btnIngredientState.setOnClickListener {
            val userId = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("userId", null) ?: return@setOnClickListener
            val ingredientName = binding.foodName.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.instance.getFridgePhoto(userId, ingredientName).execute()
                    if (response.isSuccessful) {
                        val base64 = response.body()?.string()
                        if (!base64.isNullOrEmpty()) {
                            val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            withContext(Dispatchers.Main) {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("내 재료 상태 보기")
                                    .setView(ImageView(requireContext()).apply {
                                        setImageBitmap(bitmap)
                                        layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                        )
                                    })
                                    .setPositiveButton("확인", null)
                                    .show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "이미지가 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "서버 응답 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("FOOD_IMAGE", "이미지 불러오기 실패: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }




        // 닫기 버튼
        binding.btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(name: String, imageResId: Int): FoodInfoFragment {
            val fragment = FoodInfoFragment()
            fragment.arguments = Bundle().apply {
                putString("name", name)
                putInt("imageResId", imageResId)
            }
            return fragment
        }
    }

    // 식재료 정보 조회 함수 (달라진 부분)
    private fun fetchIngredientInfo(ingredient: String) {
        val prompt = """
당신은 영양 전문가입니다.

다음 식재료에 대해 아래 세 가지 항목을 설명하세요.

요구사항:
- 각 항목을 "• "로 시작하세요.
- 한 줄에 하나의 항목만 작성하세요.
- 여러 항목을 한 줄에 이어서 쓰지 마세요. 반드시 줄바꿈하세요.
- 다른 식재료와 비교하거나 추가 설명은 하지 마세요.

식재료: "$ingredient"

1. 주요 영양 성분
2. 건강에 좋은 효능
3. 보관 방법

아래 형식으로 출력하세요:

{
  1. 영양 성분: "...",
  2. 효능: "...",
  3. 보관 방법: "..."
}

예시)
  1. 영양 성분: "• 단백질\n• 비타민 A\n• 비타민 D\n• 비타민 B12\n• 철분",
  2. 효능: "• 근육 성장 촉진\n• 면역력 강화\n• 눈 건강 증진\n• 뼈 건강 유지\n• 뇌 기능 지원",
  3. 보관 방법: "• 냉장 보관\n• 유통기한 확인\n• 껍질이 깨지지 않도록 주의"

추가 안내나 다른 텍스트는 절대 포함하지 마세요.
""".trimIndent()
        val request = ChatRequest(
            messages = listOf(Message(role = "user", content = prompt))
        )

        val apiKey = BuildConfig.API_KEY
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.openAIApi.getChatCompletion(apiKey, request).execute()
                if (response.isSuccessful) {
                    val reply = response.body()?.choices?.firstOrNull()?.message?.content ?: "응답 없음"
                    Log.d("FoodInfoFragment", "GPT 응답: $reply")
                    withContext(Dispatchers.Main) {
                        parseAndSetText(reply)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.foodNutrition.text = "API 호출 실패: ${response.code()}"
                        binding.foodEffect.text = ""
                        binding.foodStorage.text = ""
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.foodNutrition.text = "오류 발생: ${e.message}"
                    binding.foodEffect.text = ""
                    binding.foodStorage.text = ""
                }
            }
        }
    }

    // GPT 응답을 파싱해서 각각의 TextView에 설정 (달라진 부분 -> 출력 화면 보고 수정해야할수도?)
    private fun parseAndSetText(reply: String) {
        // 전체 응답을 출력 (디버깅용)
        Log.d("FoodInfoFragment", "파싱 전 원본 응답: $reply")

        val nutrition = Regex("""1\.\s*영양 성분:\s*"([^"]+)""")
            .find(reply)?.groupValues?.get(1)?.trim()?.replace("\\n", "\n")

        val effect = Regex("""2\.\s*효능:\s*"([^"]+)""")
            .find(reply)?.groupValues?.get(1)?.trim()?.replace("\\n", "\n")

        val storage = Regex("""3\.\s*보관 방법:\s*"([^"]+)""")
            .find(reply)?.groupValues?.get(1)?.trim()?.replace("\\n", "\n")

        binding.foodNutrition.text = nutrition ?: "영양 정보 없음"
        binding.foodEffect.text = effect ?: "효능 정보 없음"
        binding.foodStorage.text = storage ?: "보관 방법 없음"
    }

    private fun checkFavoriteStatus(userId: String, ingredientName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.isFavorite(userId, ingredientName)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() == true) {
                        isFavorite = true
                        binding.btnFavorite.setImageResource(R.drawable.ic_heart_red)
                    } else {
                        isFavorite = false
                        binding.btnFavorite.setImageResource(R.drawable.ic_heart_black)
                    }
                }
            } catch (e: Exception) {
                Log.e("FoodInfoFragment", "즐겨찾기 상태 확인 실패", e)
            }
        }
    }

    private fun getUserIdFromSharedPrefs(): String {
        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("userId", "") ?: ""
    }

}

