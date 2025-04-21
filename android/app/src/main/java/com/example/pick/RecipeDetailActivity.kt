package com.example.pick

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pick.databinding.ActivityRecipeDetailBinding


class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private var isSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 전달받은 레시피 이름
        val recipeName = intent.getStringExtra("recipeName") ?: ""
        binding.recipeName.text = recipeName

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.bookmarkBtn.setOnClickListener {
            isSaved = !isSaved
            binding.bookmarkBtn.setImageResource(
                if (isSaved) R.drawable.ic_bookmark_filled
                else R.drawable.ic_bookmark_border
            )
        }
    }
}