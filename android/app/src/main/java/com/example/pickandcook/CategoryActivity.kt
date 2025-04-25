package com.example.pickandcook

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pickandcook.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding

    private val categories = mapOf(
        "   종류별" to listOf(
            listOf("밑반찬", "메인반찬", "국/탕", "찌개", "디저트", "면/만두", "밥/죽/떡", "퓨전"),
            listOf("김치/젓갈/장류", "양념/소스/잼", "양식", "샐러드", "스프", "빵", "과자", "차/음료/술", "기타")
        ),
        "   상황별" to listOf(
            listOf("일상", "초스피드", "손님접대", "술안주", "다이어트", "도시락", "영양식", "간식", "야식"),
            listOf("푸드스타일링", "해장", "명절", "이유식", "기타")
        ),
        "   방법별" to listOf(
            listOf("볶음", "끓이기", "부침", "조림", "무침", "비빔", "찜", "절임", "튀김", "삶기", "굽기"),
            listOf("데치기", "회", "기타")
        )
    )
    // 그룹별 선택 항목 저장용
    private val selectedMap = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // 카테고리 UI 동적 생성
        categories.entries.forEachIndexed { index, (groupName, groupedRows) ->

            // 그룹명 (제목 텍스트)
            val titleView = TextView(this).apply {
                text = groupName
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 23f)
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.DKGRAY)
                setPadding(0, 32, 0, 8)
            }
            binding.category.addView(titleView)

            groupedRows.forEach { rowList ->
                val rowLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 8, 0, 8) }
                }

                rowList.forEach { category ->
                    val tv = TextView(this).apply {
                        text = category
                        setPadding(24, 12, 24, 12)
                        background = ContextCompat.getDrawable(this@CategoryActivity, R.drawable.default_border)
                        setTextColor(Color.BLACK)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply { setMargins(8, 8, 8, 8) }

                        tag = "$groupName:$category"

                        setOnClickListener {
                            val previous = selectedMap[groupName]
                            if (previous == category) {
                                selectedMap.remove(groupName)
                                background = ContextCompat.getDrawable(this@CategoryActivity, R.drawable.default_border)
                            } else {
                                // 기존 선택 해제
                                groupedRows.flatten().forEach { item ->
                                    binding.category.findViewWithTag<TextView>("$groupName:$item")
                                        ?.background = ContextCompat.getDrawable(this@CategoryActivity, R.drawable.default_border)
                                }
                                selectedMap[groupName] = category
                                background = ContextCompat.getDrawable(this@CategoryActivity, R.drawable.red_border)
                            }
                        }
                    }
                    rowLayout.addView(tv)
                }

                binding.category.addView(rowLayout)
            }

            // 그룹 간 구분선 추가 (마지막 그룹 제외)
            if (index < categories.size - 1) {
                val dividerContainer = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    gravity = android.view.Gravity.CENTER_HORIZONTAL
                }

                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        dpToPx(1000),
                        2
                    ).apply {
                        topMargin = 28
                        bottomMargin = 28
                    }
                    setBackgroundColor(Color.LTGRAY)
                }

                dividerContainer.addView(divider)
                binding.category.addView(dividerContainer)
            }


        }


        // 카테고리 넘기는 작업 해야함 (어디로?)
        binding.btnNext.setOnClickListener {
            if (selectedMap.isEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("카테고리를 하나 이상 선택해주세요.")
                    .setPositiveButton("확인", null)
                    .show()
                return@setOnClickListener
            }
            Toast.makeText(this, selectedMap.values.joinToString(), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RecipeResultActivity::class.java)
            startActivity(intent)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

}

