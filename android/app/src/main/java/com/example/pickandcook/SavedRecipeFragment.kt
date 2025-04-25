package com.example.pickandcook

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pickandcook.databinding.FragmentSavedRecipeBinding

class SavedRecipeFragment : Fragment() {

    private var _binding: FragmentSavedRecipeBinding? = null
    private val binding get() = _binding!!

    private val recipeList = listOf("채소 볶음", "비빔밥", "달걀말이")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeList.forEach { recipeName ->
            val tv = TextView(requireContext()).apply {
                text = recipeName
                setPadding(48, 60, 48, 60)
                textSize = 22f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.BLACK)
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_rounded)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(12, 20, 12, 20)
                }
                layoutParams = params
                gravity = Gravity.CENTER

                setOnClickListener {
                    val intent = Intent(requireActivity(), RecipeDetailActivity::class.java).apply {
                        putExtra("recipeName", recipeName)
                    }
                    startActivity(intent)
                }

            }

            binding.recipeContainer.addView(tv)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
