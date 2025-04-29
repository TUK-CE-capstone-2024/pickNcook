package com.example.pickandcook

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pickandcook.api.RecipeItem
import com.example.pickandcook.databinding.FragmentRecipeResultBinding

class RecipeResultFragment : Fragment() {

    private var _binding: FragmentRecipeResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val recipeList = arguments?.getParcelableArrayList<RecipeItem>("recipes") ?: arrayListOf()

        recipeList.forEach { recipeItem ->
            val tv = TextView(requireContext()).apply {
                text = recipeItem.recipeName
                setPadding(40, 60, 40, 60)
                textSize = 18f
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
                    val intent = Intent(requireContext(), RecipeDetailActivity::class.java).apply {
                        putExtra("recipeNo", recipeItem.recipeNo)
                        putExtra("recipeName", recipeItem.recipeName)
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
