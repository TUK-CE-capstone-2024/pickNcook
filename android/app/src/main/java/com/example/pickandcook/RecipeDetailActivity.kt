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
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.pickandcook.api.Recipe

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private var isSaved = false
    private var recipeNo: Int = -1
    private lateinit var recipeTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeNo = intent.getIntExtra("recipeNo", -1)
        val userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("userId", null) ?: ""

        checkIfRecipeIsSaved(userId, recipeNo)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.compareFridgeBtn.setOnClickListener {
            RetrofitClient.instance.getRecipeByNo(recipeNo).enqueue(object : Callback<Recipe> {
                override fun onResponse(call: Call<Recipe>, response: Response<Recipe>) {
                    if (response.isSuccessful) {
                        val ckgMtrlCn = response.body()?.ckgMtrlCn ?: ""
                        compareWithFridge(ckgMtrlCn)
                    } else {
                        Toast.makeText(this@RecipeDetailActivity, "레시피 불러오기 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Recipe>, t: Throwable) {
                    Toast.makeText(this@RecipeDetailActivity, "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        CoroutineScope(Dispatchers.IO).launch {
            val recipeData = RetrofitClient.instance.getRecipeByNo(recipeNo).execute().body()
            if (recipeData != null) {
                recipeTitle = recipeData.rcpTtl ?: ""
                val imageUrl = recipeData.rcpImgUrl

                withContext(Dispatchers.Main) {
                    binding.recipeName.text = recipeTitle
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this@RecipeDetailActivity)
                            .load(imageUrl)
                            .into(binding.recipeImageView)
                    }
                }

                fetchRecipeDetails(recipeTitle)
            } else {
                withContext(Dispatchers.Main) {
                    showError("레시피 정보를 불러올 수 없습니다.")
                }
            }
        }

        binding.bookmarkBtn.setOnClickListener {
            if (isSaved) {
                deleteRecipeFromStorage()
            } else {
                saveRecipeToStorage()
            }
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

                // 레시피 순서에 번호 매기기
                val steps = doc.select("div.view_step div.media-body")
                    .mapIndexed { index, element ->
                        "${index + 1} . ${element.ownText().trim()}\n"
                    }.joinToString("\n")

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

    private fun checkIfRecipeIsSaved(userId: String, recipeNo: Int) {
        val call = RetrofitClient.instance.isRecipeSaved(userId, recipeNo)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() == true) {
                    binding.bookmarkBtn.setImageResource(R.drawable.ic_bookmark_filled)
                    isSaved = true
                } else {
                    binding.bookmarkBtn.setImageResource(R.drawable.ic_bookmark_border)
                    isSaved = false
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Toast.makeText(this@RecipeDetailActivity, "서버 통신 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteRecipeFromStorage() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.deleteRecipe(userId, recipeNo)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RecipeDetailActivity, "레시피 삭제됨", Toast.LENGTH_SHORT).show()
                        isSaved = false
                        binding.bookmarkBtn.setImageResource(R.drawable.ic_bookmark_border)
                    } else {
                        Log.d("DELETE", "Try delete: userId=$userId, recipeNo=$recipeNo")
                        Log.d("DELETE", "code=${response.code()}, success=${response.isSuccessful}")

                        Toast.makeText(this@RecipeDetailActivity, "삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@RecipeDetailActivity, "오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    /*
    private fun compareWithFridge(ingredientsText: String) {
        val userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("userId", null) ?: return

        val recipeIngredients = extractIngredients(ingredientsText)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getFridgeItems(userId).execute()
                if (response.isSuccessful) {
                    val fridgeItems = response.body() ?: emptyList()
                    val fridgeIngredients = fridgeItems.map { it.fridgeIngredient.trim() }

                    val missingIngredients = recipeIngredients.filter { it !in fridgeIngredients }

                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@RecipeDetailActivity)
                            .setTitle("없는 재료")
                            .setMessage(
                                if (missingIngredients.isEmpty()) "모든 재료를 가지고 있습니다!"
                                else missingIngredients.joinToString(", ")
                            )
                            .setPositiveButton("확인", null)
                            .show()
                    }
                } else {
                    Log.e("CompareFridge", "냉장고 조회 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CompareFridge", "에러 발생: ${e.message}")
            }
        }
    }
     */
    private fun compareWithFridge(ckgMtrlCn: String) {
        val userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("userId", null) ?: return
        val recipeIngredients = ckgMtrlCn.split("|").map { it.trim() }

        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.instance.getFridgeItems(userId).execute()
            if (response.isSuccessful) {
                val fridgeIngredients = response.body()?.map { it.fridgeIngredient.trim() } ?: emptyList()
                val missingIngredients = recipeIngredients.filter { it !in fridgeIngredients }

                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(this@RecipeDetailActivity)
                        .setTitle("없는 재료")
                        .setMessage(
                            if (missingIngredients.isEmpty()) "모든 재료를 가지고 있습니다!"
                            else missingIngredients.joinToString(", ")
                        )
                        .setPositiveButton("확인", null)
                        .show()
                }
            }
        }
    }


    private fun extractIngredients(rawText: String): List<String> {
        return rawText.lines()
            .mapNotNull { line ->
                Regex("-\\s*(.+?)\\s[\\d/]+[a-zA-Z가-힣]*").find(line)?.groupValues?.get(1)?.trim()
            }
    }



}
