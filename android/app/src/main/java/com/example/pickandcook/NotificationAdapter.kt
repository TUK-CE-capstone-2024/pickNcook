package com.example.pickandcook

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.pickandcook.databinding.ItemNotificationBinding
import com.example.pickandcook.api.Notification
import com.example.pickandcook.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationAdapter(
    private val list: MutableList<Notification>,
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notification) {
            binding.textMessage.text = item.notificationMsg
            binding.textDate.text = formatDate(item.regDate)


            binding.btnClose.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    RetrofitClient.instance.deleteNotification(item.notificationNo)
                        .enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    removeItem(item)
                                    Toast.makeText(binding.root.context, "알림이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(binding.root.context, "삭제 실패", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Toast.makeText(binding.root.context, "오류: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun removeItem(item: Notification) {
        val position = list.indexOf(item)
        if (position != -1) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): Notification = list[position]

    fun formatDate(rawDate: String): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            val date = inputFormat.parse(rawDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            rawDate // 파싱 실패 시 원본 출력
        }
    }


}
