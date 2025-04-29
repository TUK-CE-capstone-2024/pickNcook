package com.example.pickandcook

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.api.SaveRecipeRequest
import com.example.pickandcook.api.SaveRecipeResponse
import com.example.pickandcook.databinding.ActivityRecipeDetailBinding
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class RecipeDetailActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRecipeDetailBinding
    private var isSaved = false
    private lateinit var recipeName: String   // 레시피 이름 저장용
    private var recipeNo: Int = -1   // 레시피 번호 저장용

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. 툴바 뒤로가기
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // 2. 레시피 이름 받아오기
        recipeNo = intent.getIntExtra("recipeNo", -1)
        recipeName = intent.getStringExtra("recipeName") ?: ""
        binding.recipeName.text = recipeName


        // db에서 이미지 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            val imageUrl = MySQLHelper.getRecipeImageUrl(recipeName)
            withContext(Dispatchers.Main) {
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this@RecipeDetailActivity)
                        .load(imageUrl)
                        .into(binding.recipeImageView)
                }
            }
        }

        // 3. 북마크 버튼 클릭 (서버 저장 요청)
        binding.bookmarkBtn.setOnClickListener {
            saveRecipeToStorage()
        }

        // 4. 레시피 크롤링 및 화면 출력
        fetchRecipeDetails(recipeName)
    }

    /**
     * 레시피 크롤링 및 표시 함수
     */
    private fun fetchRecipeDetails(keyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. 검색 결과 중 첫 번째 링크 가져오기
                val recipeUrl = searchRecipeLink(keyword)
                if (recipeUrl == null) {
                    withContext(Dispatchers.Main) {
                        showError("레시피를 찾을 수 없습니다.")
                    }
                    return@launch
                }

                // 2. Jsoup 크롤링
                val doc = Jsoup.connect(recipeUrl).userAgent("Mozilla/5.0").get()
                val title = doc.selectFirst("h1.view2_summary_stitle")?.text()?.trim() ?: keyword

                val ingredientUl = doc.select("div.ready_ingre3 ul").first()
                val ingredients = ingredientUl?.select("li")?.joinToString("\n") {
                    "- ${it.text().replace("구매", "").trim()}"
                } ?: "재료 정보 없음"

                val steps = doc.select("div.view_step div.media-body").joinToString("\n") {
                    "• ${it.ownText().trim()}"
                }

                val resultText = """
🍽 $title

📋 재료:
$ingredients

👨‍🍳 방법:
$steps

🔗 출처: $recipeUrl
""".trimIndent()

                // 3. UI 업데이트
                withContext(Dispatchers.Main) {
                    binding.recipeName.text = title
                    // binding.infoCard.removeAllViews() 삭제!

                    val detailView = TextView(this@RecipeDetailActivity).apply {
                        text = resultText
                        textSize = 15f
                        setLineSpacing(12f, 1.3f)
                        setPadding(0, 150, 0, 0)
                        autoLinkMask = Linkify.WEB_URLS
                        movementMethod = LinkMovementMethod.getInstance()
                    }
                    binding.infoCard.addView(detailView)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("오류 발생: ${e.message}")
                }
            }
        }
    }

    /**
     * 검색 링크 얻어오기 함수
     */
    private fun searchRecipeLink(keyword: String): String? {
        val encoded = URLEncoder.encode(keyword, "UTF-8")
        val url = "https://www.10000recipe.com/recipe/list.html?q=$encoded&order=accuracy"
        val doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        val link = doc.select("ul.common_sp_list_ul li.common_sp_list_li a").firstOrNull()?.attr("href")
        return if (link != null) "https://www.10000recipe.com$link" else null
    }

    /**
     * 에러 메시지 표시 함수
     */
    private fun showError(message: String) {
        val errorView = TextView(this).apply {
            text = message
            textSize = 18f
            setTextColor(android.graphics.Color.RED)
        }
        binding.infoCard.addView(errorView)
    }

    /**
     * 서버에 레시피 저장 요청 보내기
     */
    private fun saveRecipeToStorage() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = SaveRecipeRequest(recipeNo, userId)

        RetrofitClient.instance.saveRecipe(request)
            .enqueue(object : Callback<SaveRecipeResponse> {
                override fun onResponse(
                    call: Call<SaveRecipeResponse>,
                    response: Response<SaveRecipeResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RecipeDetailActivity, "저장 완료", Toast.LENGTH_SHORT).show()
                        isSaved = true
                        binding.bookmarkBtn.setImageResource(R.drawable.ic_bookmark_filled)
                    } else {
                        Toast.makeText(this@RecipeDetailActivity, "저장 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SaveRecipeResponse>, t: Throwable) {
                    Toast.makeText(this@RecipeDetailActivity, "오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
