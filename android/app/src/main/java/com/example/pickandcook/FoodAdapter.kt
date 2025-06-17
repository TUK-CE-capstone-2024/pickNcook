package com.example.pickandcook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pickandcook.databinding.ItemFoodBinding

class FoodAdapter(
    private val items: List<FoodItem>,
    private val onItemClick: (FoodItem) -> Unit,
    private val enableSelection: Boolean = false, // 선택 기능 ON/OFF
    private val onDeleteClick: ((FoodItem) -> Unit)? = null, // 삭제 콜백 (선택)
    private val showDeleteButton: Boolean = false // 삭제 버튼 표시 여부
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    // 선택된 식재료 리스트
    private val selectedItems = mutableSetOf<FoodItem>()

    inner class FoodViewHolder(private val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FoodItem, isSelected: Boolean) {
            binding.foodName.text = item.name
            binding.foodImage.setImageResource(item.imageResId)

            // 삭제 버튼 표시 여부 처리
            binding.btnDelete.visibility = if (showDeleteButton) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                onItemClick(item)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick?.invoke(item)
            }

            // 선택 상태에 따른 배경
            binding.root.isSelected = enableSelection && isSelected

            binding.root.setOnClickListener {
                if (enableSelection) {
                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item)
                    } else {
                        selectedItems.add(item)
                    }
                    notifyItemChanged(adapterPosition)
                }
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = items[position]
        val isSelected = selectedItems.contains(item)
        holder.bind(item, isSelected)
    }

    override fun getItemCount(): Int = items.size

    // 외부에서 선택된 식재료 가져오기
    fun getSelectedItems(): List<FoodItem> = selectedItems.toList()
}
