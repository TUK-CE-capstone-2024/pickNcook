package com.example.pickandcook

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pickandcook.api.RecipeItem
import com.example.pickandcook.databinding.ActivityRecipeResultBinding

class RecipeResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val recipeList = intent.getParcelableArrayListExtra<RecipeItem>("recipes") ?: arrayListOf()

        recipeList.forEach { recipeItem ->
            val tv = TextView(this).apply {
                text = recipeItem.recipeName
                setPadding(48, 60, 48, 60)
                textSize = 22f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.BLACK)
                background = ContextCompat.getDrawable(this@RecipeResultActivity, R.drawable.bg_rounded)

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(12, 20, 12, 20)
                }
                layoutParams = params
                gravity = Gravity.CENTER

                setOnClickListener {
                    val intent = Intent(this@RecipeResultActivity, RecipeDetailActivity::class.java).apply {
                        putExtra("recipeNo", recipeItem.recipeNo)
                        putExtra("recipeName", recipeItem.recipeName)
                    }
                    startActivity(intent)
                }
            }
            binding.recipeContainer.addView(tv)
        }
    }
}
