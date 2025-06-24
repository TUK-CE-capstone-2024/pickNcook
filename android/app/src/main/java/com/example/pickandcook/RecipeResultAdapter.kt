package com.example.pickandcook

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pickandcook.api.Recipe
import com.example.pickandcook.api.RecipeDetailResponse
import com.example.pickandcook.api.RecipeItem
import com.example.pickandcook.databinding.ItemRecipeBinding

class RecipeResultAdapter(
    private val items: List<Recipe>
) : RecyclerView.Adapter<RecipeResultAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.recipeTitle.text = item.rcpTtl
            binding.categoryText.text = "${item.ckgKndActoNm} / ${item.ckgStaActoNm} / ${item.ckgMthActoNm}"
            binding.portionText.text = item.ckgInbunNm
            binding.timeText.text = item.ckgTimeNm
            binding.difficultyText.text = item.ckgDodfNm

            Glide.with(binding.recipeImage.context)
                .load(item.rcpImgUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.recipeImage)

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, RecipeDetailActivity::class.java).apply {
                    putExtra("recipeNo", item.recipeNo)
                    putExtra("recipeName", item.ckgNm)
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