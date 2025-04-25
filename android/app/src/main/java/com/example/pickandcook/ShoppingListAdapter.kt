package com.example.pickandcook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pickandcook.api.ShoppingList

class ShoppingListAdapter(
    private val lists: List<ShoppingList>,
    private val onItemClick: (ShoppingList) -> Unit,
    private val onDeleteClick: (ShoppingList) -> Unit // 삭제 버튼 클릭용
) : RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listName: TextView = itemView.findViewById(R.id.tvListName)
        val regDate: TextView = itemView.findViewById(R.id.tvRegDate)
        val deleteButton: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(item: ShoppingList) {
            listName.text = item.listName
            regDate.text = item.regDate
            itemView.setOnClickListener { onItemClick(item) }
            deleteButton.setOnClickListener { onDeleteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lists[position])
    }
}
