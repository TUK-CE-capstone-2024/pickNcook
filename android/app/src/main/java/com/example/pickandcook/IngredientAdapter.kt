package com.example.pickandcook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pickandcook.api.ShoppingListDetail

class IngredientAdapter(
    private val ingredients: List<ShoppingListDetail>,
    private val onDeleteClick: (ShoppingListDetail) -> Unit
) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientName: TextView = view.findViewById(R.id.tvIngredient)
        val deleteButton: Button = view.findViewById(R.id.btnDeleteIngredient)

        fun bind(item: ShoppingListDetail) {
            ingredientName.text = item.ingredientName
            deleteButton.setOnClickListener { onDeleteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = ingredients.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }
}
