package com.example.pick

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pick.databinding.ItemShoppingBinding

class ShoppingAdapter(
    private val items: MutableList<ShoppingItem>,
    private val onDelete: (ShoppingItem) -> Unit,
    private val enableSelection: Boolean = false, // 선택 기능 ON/OFF
    private val enableDelete: Boolean = true // 삭제 버튼 표시 여부
) : RecyclerView.Adapter<ShoppingAdapter.ViewHolder>() {

    // 선택 식재료 저장
    private val selectedItems = mutableSetOf<ShoppingItem>()

    inner class ViewHolder(private val binding: ItemShoppingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingItem, isSelected: Boolean) {
            binding.itemName.text = item.name
            binding.warningIcon.visibility = if (item.showWarning) View.VISIBLE else View.GONE
            binding.root.isSelected = enableSelection && isSelected

            // 삭제 버튼 표시 여부 제어
            binding.deleteIcon.visibility = if (enableDelete) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                if (enableSelection) {
                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item)
                    } else {
                        selectedItems.add(item)
                    }
                    notifyItemChanged(adapterPosition)
                }
            }
            // 식재료 삭제
            binding.deleteIcon.setOnClickListener {
                if (enableDelete) onDelete(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val isSelected = selectedItems.contains(item)
        holder.bind(item, isSelected)
    }

    override fun getItemCount(): Int = items.size

    fun removeItem(item: ShoppingItem) {
        val index = items.indexOf(item)
        if (index != -1) {
            items.removeAt(index)
            selectedItems.remove(item)
            notifyItemRemoved(index)
        }
    }
    // 외부에서 선택된 식재료 가져오기
    fun getSelectedItems(): List<ShoppingItem> = selectedItems.toList()
}
