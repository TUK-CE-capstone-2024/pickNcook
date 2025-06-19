package com.example.pickandcook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pickandcook.api.ShoppingListDetail
import com.example.pickandcook.databinding.ItemIngredientBinding

// 쇼핑리스트때 필요할걸?
class IngredientAdapter(
    private val ingredients: List<ShoppingListDetail>,
    private val onDeleteClick: (ShoppingListDetail) -> Unit
) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemIngredientBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingListDetail) {
            binding.tvIngredient.text = item.ingredientName
            binding.btnClose.setOnClickListener { onDeleteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = ingredients.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }
}
