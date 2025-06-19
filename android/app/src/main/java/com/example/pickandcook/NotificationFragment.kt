package com.example.pickandcook

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pickandcook.api.Notification
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.databinding.FragmentNotificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()

        // 알림창 들어갈 때 마지막 확인 시간 저장
        val prefs = requireContext().getSharedPreferences("NotiPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("lastSeenTime", getCurrentTimeISOString()).apply()

        val userId = requireActivity()
            .getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("userId", null)

        if (userId != null) {
            fetchNotifications(userId)
        } else {
            Toast.makeText(requireContext(), "로그인이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchNotifications(userId: String) {
        RetrofitClient.instance.getUserNotifications(userId)
            .enqueue(object : Callback<List<Notification>> {
                override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                    if (response.isSuccessful) {
                        val notifications = response.body() ?: emptyList()

                        adapter = NotificationAdapter(notifications.toMutableList())
                        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                        binding.recyclerView.adapter = adapter

                        // 밀어서 삭제
                        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                            override fun onMove(
                                recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder
                            ): Boolean = false

                            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                                val position = viewHolder.adapterPosition
                                val item = adapter.getItem(position)

                                RetrofitClient.instance.deleteNotification(item.notificationNo)
                                    .enqueue(object : Callback<Void> {
                                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                            if (response.isSuccessful) {
                                                adapter.removeItem(item)
                                                Toast.makeText(requireContext(), "삭제 완료", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
                                                adapter.notifyItemChanged(position)
                                            }
                                        }

                                        override fun onFailure(call: Call<Void>, t: Throwable) {
                                            Toast.makeText(requireContext(), "오류: ${t.message}", Toast.LENGTH_SHORT).show()
                                            adapter.notifyItemChanged(position)
                                        }
                                    })
                            }
                        })
                        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

                    } else {
                        Toast.makeText(requireContext(), "알림 불러오기 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // 알림창 들어갈 때 마지막 확인 시간 저장
    private fun getCurrentTimeISOString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        return sdf.format(Date())
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
