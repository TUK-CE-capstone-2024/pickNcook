package com.example.pickandcook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.pickandcook.api.Notification
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 클릭 시 전체 화면
        binding.fridgeView.setOnClickListener {
            val fragment = FridgeFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.cartView.setOnClickListener {
            val fragment = ShoppingCartFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.recipeButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, SelectIngredientFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.notificationIcon.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, NotificationFragment())
                .addToBackStack(null)
                .commit()
        }

        // 알림 최신 여부 체크하고 빨간 점 표시
        val prefs = requireContext().getSharedPreferences("NotiPrefs", Context.MODE_PRIVATE)
        val lastSeenTime = prefs.getString("lastSeenTime", null)

        val userId = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            .getString("userId", null)

        if (userId != null) {
            RetrofitClient.instance.getUserNotifications(userId)
                .enqueue(object : Callback<List<Notification>> {
                    override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                        if (!isAdded || _binding == null) return // 🔒 프래그먼트가 살아있을 때만 처리

                        if (response.isSuccessful) {
                            val notifications = response.body() ?: emptyList()
                            val newestTime = notifications.maxOfOrNull { it.regDate }

                            if (newestTime != null && lastSeenTime != null) {
                                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                                val newestDate = sdf.parse(newestTime)
                                val lastSeenDate = sdf.parse(lastSeenTime)

                                if (newestDate != null && lastSeenDate != null && newestDate.after(lastSeenDate)) {
                                    binding.redDot.visibility = View.VISIBLE
                                } else {
                                    binding.redDot.visibility = View.GONE
                                }
                            } else {
                                binding.redDot.visibility = if (newestTime != null) View.VISIBLE else View.GONE
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                        // 네트워크 오류 시 무시
                    }
                })

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
