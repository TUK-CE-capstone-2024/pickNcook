package com.example.pickandcook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pickandcook.api.BarcodeResponse
import com.example.pickandcook.databinding.ItemShoppingBinding

class ShoppingAdapter(
    private val items: MutableList<ShoppingItem>,
    private val onDelete: (BarcodeResponse) -> Unit,
    private val enableSelection: Boolean = false,
    private val enableDelete: Boolean = true
) : RecyclerView.Adapter<ShoppingAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<ShoppingItem>()

    inner class ViewHolder(private val binding: ItemShoppingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingItem, isSelected: Boolean) {
            binding.itemName.text = item.name  // ✔ itemName으로 수정
            binding.warningIcon.visibility = if (item.showWarning) View.VISIBLE else View.GONE

            // 선택 활성화 시 배경 처리
            if (enableSelection) {
                binding.root.isSelected = isSelected
                binding.root.setBackgroundResource(
                    if (isSelected) R.drawable.red_border
                    else R.drawable.item_background_selector
                )
            }

            binding.deleteIcon.visibility = if (enableDelete) View.VISIBLE else View.GONE
            binding.deleteIcon.setOnClickListener {
                onDelete(
                    BarcodeResponse(
                        barcodeNum = "",   // 실제 barcodeNum 필요하면 추가 로직 필요
                        ingredientName = item.name,
                        price = 0
                    )
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
        val binding = ItemShoppingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, selectedItems.contains(item))
    }

    fun updateItems(newItems: List<ShoppingItem>) {
        items.clear()
        items.addAll(newItems)
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<ShoppingItem> = selectedItems.toList()

    private fun toggleSelection(item: ShoppingItem) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
        } else {
            selectedItems.add(item)
        }
        notifyDataSetChanged()
    }
}
