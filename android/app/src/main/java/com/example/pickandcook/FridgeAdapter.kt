package com.example.pickandcook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pickandcook.api.FridgeItem
import com.example.pickandcook.databinding.ItemFoodBinding

class FridgeAdapter(
    private val items: MutableList<FridgeItem>,
    private val enableSelection: Boolean = false
) : RecyclerView.Adapter<FridgeAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<FridgeItem>()

    inner class ViewHolder(private val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FridgeItem, isSelected: Boolean) {
            binding.foodName.text = item.fridgeIngredient

            // 선택 상태에 따라 배경 변경
            if (enableSelection) {
                binding.foodItemContainer.isSelected = isSelected
                binding.foodItemContainer.setBackgroundResource(
                    if (isSelected) R.drawable.red_border
                    else R.drawable.item_background_selector
                )
            }

            binding.root.setOnClickListener {
                if (enableSelection) {
                    toggleSelection(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, selectedItems.contains(item))
    }

    fun updateItems(newItems: List<FridgeItem>) {
        items.clear()
        items.addAll(newItems)
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<FridgeItem> = selectedItems.toList()

    private fun toggleSelection(item: FridgeItem) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
        } else {
            selectedItems.add(item)
        }
        notifyDataSetChanged()
    }
}
