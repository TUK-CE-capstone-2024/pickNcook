package com.example.pickandcook

import android.content.Context
import android.os.Bundle
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
    private var recipeNo: Int = -1
    private lateinit var recipeTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        recipeNo = intent.getIntExtra("recipeNo", -1)

        // DB에서 제목, 이미지 등 정보 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            val recipeData = MySQLHelper.getRecipeByNo(recipeNo)  // 아래에 정의해둘 것
            if (recipeData != null) {
                recipeTitle = recipeData.rcpTtl
                val imageUrl = recipeData.rcpImgUrl

                withContext(Dispatchers.Main) {
                    binding.recipeName.text = recipeTitle
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this@RecipeDetailActivity)
                            .load(imageUrl)
                            .into(binding.recipeImageView)
                    }
                }

                // 이제 정확한 제목으로 크롤링 시작
                fetchRecipeDetails(recipeTitle)
            } else {
                withContext(Dispatchers.Main) {
                    showError("레시피 정보를 불러올 수 없습니다.")
                }
            }
        }

        binding.bookmarkBtn.setOnClickListener {
            saveRecipeToStorage()
        }
    }

    private fun fetchRecipeDetails(keyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val recipeUrl = searchRecipeLink(keyword)
                if (recipeUrl == null) {
                    withContext(Dispatchers.Main) {
                        showError("레시피를 찾을 수 없습니다.")
                    }
                    return@launch
                }

                val doc = Jsoup.connect(recipeUrl).userAgent("Mozilla/5.0").get()
                val title = doc.selectFirst("h1.view2_summary_stitle")?.text()?.trim() ?: keyword

                val ingredientUl = doc.select("div.ready_ingre3 ul").first()
                val ingredients = ingredientUl?.select("li")?.joinToString("\n") {
                    "- ${it.text().replace("구매", "").trim()}"
                } ?: "재료 정보 없음"

                val steps = doc.select("div.view_step div.media-body").joinToString("\n") {
                    "• ${it.ownText().trim()}"
                }

                withContext(Dispatchers.Main) {
                    binding.recipeName.text = title
                    binding.recipeIngredients.text = ingredients
                    binding.recipeSteps.text = steps
                    binding.recipeSource.text = recipeUrl
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("오류 발생: ${e.message}")
                }
            }
        }
    }

    private fun searchRecipeLink(keyword: String): String? {
        val encoded = URLEncoder.encode(keyword, "UTF-8")
        val url = "https://www.10000recipe.com/recipe/list.html?q=$encoded&order=accuracy"
        val doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get()
        val recipes = doc.select("ul.common_sp_list_ul li.common_sp_list_li")

        for (recipe in recipes) {
            val title = recipe.selectFirst(".common_sp_caption_tit")?.text()?.trim()
            if (title != null && title.contains(keyword)) {
                val link = recipe.selectFirst("a")?.attr("href")
                if (link != null) {
                    return "https://www.10000recipe.com$link"
                }
            }
        }

        return recipes.firstOrNull()?.selectFirst("a")?.attr("href")?.let {
            "https://www.10000recipe.com$it"
        }
    }

    private fun showError(message: String) {
        val errorView = TextView(this).apply {
            text = message
            textSize = 18f
            setTextColor(android.graphics.Color.RED)
        }
        binding.infoCard.addView(errorView)
    }

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
