package com.example.pickandcook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pickandcook.databinding.FragmentFoodInfoBinding
import com.example.pickandcook.api.* // ChatGPT API 의존성
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        binding.foodName.text = name
        binding.foodImage.setImageResource(imageResId)

        // 식재료 정보를 GPT API로 가져와서 표시 (달라진 부분 -> 식재료 정보들도 가져와야함)
        fetchIngredientInfo(name)

        // 하트 클릭 시 선호식품 표시
        binding.btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_heart_red
                else R.drawable.ic_heart_black
            )
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

            다음 식재료에 대해 아래 세 가지 항목만 설명하세요. **다른 식재료와 비교하지 마세요. 일반적인 설명 없이, 지정된 식재료에 대해서만 대답하세요.**

            식재료: "$ingredient"

            1. 주요 영양 성분
            2. 건강에 좋은 효능
            3. 보관 방법

            다음 형식을 꼭 지키세요. 번호와 항목 제목을 포함해 한글로 출력하세요:

            {
              1. 영양 성분: "...",
              2. 효능: "...",
              3. 보관 방법: "..."
            }

            다른 말은 하지 마세요.
        """.trimIndent()

        val request = ChatRequest(
            messages = listOf(Message(role = "user", content = prompt))
        )



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

        val nutrition = Regex("""1\.\s*영양 성분:\s*"?(.*?)"?\s*(,|\})""")
            .find(reply)?.groupValues?.get(1)?.trim()

        val effect = Regex("""2\.\s*효능:\s*"?(.*?)"?\s*(,|\})""")
            .find(reply)?.groupValues?.get(1)?.trim()

        val storage = Regex("""3\.\s*보관 방법:\s*"?(.*?)"?\s*(,|\})""")
            .find(reply)?.groupValues?.get(1)?.trim()

        binding.foodNutrition.text = nutrition ?: "영양 정보 없음"
        binding.foodEffect.text = effect ?: "효능 정보 없음"
        binding.foodStorage.text = storage ?: "보관 방법 없음"
    }

}

