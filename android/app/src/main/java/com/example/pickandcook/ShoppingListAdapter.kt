package com.example.pickandcook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pickandcook.api.ShoppingList
import com.example.pickandcook.databinding.ItemShoppingListBinding

class ShoppingListAdapter(
    private val lists: List<ShoppingList>,
    private val onItemClick: (ShoppingList) -> Unit,
    private val onDeleteClick: (ShoppingList) -> Unit // 삭제 버튼 클릭용
) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemShoppingListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingList) {
            binding.tvListName.text = item.listName
            binding.tvRegDate.text = item.regDate
            binding.root.setOnClickListener { onItemClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position])
    }
}
