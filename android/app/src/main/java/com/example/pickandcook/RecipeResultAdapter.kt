package com.example.pickandcook

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pickandcook.api.RecipeItem
import com.example.pickandcook.databinding.ItemRecipeBinding

class RecipeResultAdapter(
    private val items: List<RecipeItem>
) : RecyclerView.Adapter<RecipeResultAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecipeItem) {
            binding.recipeTitle.text = item.recipeName
            binding.categoryText.text = "종류별 / 상황별 / 방법별"
            binding.portionText.text = "1인분"
            binding.timeText.text = "5분이내"
            binding.difficultyText.text = "아무나"
            binding.recipeImage.setImageResource(R.drawable.ic_placeholder)

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, RecipeDetailActivity::class.java).apply {
                    putExtra("recipeNo", item.recipeNo)
                    putExtra("recipeName", item.recipeName)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
