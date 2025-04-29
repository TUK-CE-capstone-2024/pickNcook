package com.example.pickandcook

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.pickandcook.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupCategoryUI()

        // 카테고리 넘기는 작업
        binding.btnNext.setOnClickListener {
            if (selectedMap.isEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("알림")
                    .setMessage("카테고리를 하나 이상 선택해주세요.")
                    .setPositiveButton("확인", null)
                    .show()
                return@setOnClickListener
            }
            Toast.makeText(requireContext(), selectedMap.values.joinToString(), Toast.LENGTH_SHORT).show()

            // 카테고리 선택 값 넣기 (달라진 부분 -> 어디로 해결)
            val bundle = Bundle().apply {
                putString("category_kind", selectedMap["   종류별"])
                putString("category_situation", selectedMap["   상황별"])
                putString("category_method", selectedMap["   방법별"])
            }

            parentFragmentManager.commit {
                replace(R.id.mainFragmentContainer, RecipeRecommendationFragment().apply { arguments = bundle })
                addToBackStack(null)
            }
        }
    }

    // 카테고리 UI 동적 생성
    private fun setupCategoryUI() {
        categories.entries.forEachIndexed { index, (groupName, groupedRows) ->

            // 그룹명 (제목 텍스트)
            val titleView = TextView(requireContext()).apply {
                text = groupName
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.DKGRAY)
                setPadding(0, 20, 0, 8)
            }
            binding.category.addView(titleView)

            groupedRows.forEach { rowList ->
                val rowLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 7, 0, 7) }
                }

                rowList.forEach { category ->
                    val tv = TextView(requireContext()).apply {
                        text = category
                        setPadding(15, 6, 15, 6)
                        background = ContextCompat.getDrawable(requireContext(), R.drawable.default_border)
                        setTextColor(Color.BLACK)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                        setTypeface(null, Typeface.NORMAL)
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply { setMargins(7, 7, 7, 7) }
                        tag = "$groupName:$category"

                        setOnClickListener {
                            val previous = selectedMap[groupName]
                            if (previous == category) {
                                selectedMap.remove(groupName)
                                background = ContextCompat.getDrawable(requireContext(), R.drawable.default_border)
                            } else {
                                // 기존 선택 해제
                                groupedRows.flatten().forEach { item ->
                                    binding.category.findViewWithTag<TextView>("$groupName:$item")
                                        ?.background = ContextCompat.getDrawable(requireContext(), R.drawable.default_border)
                                }
                                selectedMap[groupName] = category
                                background = ContextCompat.getDrawable(requireContext(), R.drawable.red_border)
                            }
                        }
                    }
                    rowLayout.addView(tv)
                }
                binding.category.addView(rowLayout)
            }

            // 그룹 간 구분선 추가 (마지막 그룹 제외)
            if (index < categories.size - 1) {
                val dividerContainer = LinearLayout(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    gravity = Gravity.CENTER_HORIZONTAL
                }

                val divider = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        dpToPx(650),
                        2
                    ).apply {
                        topMargin = 20
                        bottomMargin = 20
                    }
                    setBackgroundColor(Color.LTGRAY)
                }

                dividerContainer.addView(divider)
                binding.category.addView(dividerContainer)
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
