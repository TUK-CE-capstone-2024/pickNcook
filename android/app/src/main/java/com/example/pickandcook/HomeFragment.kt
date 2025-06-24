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
import java.util.Date
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

        // 알림빨간 최신 여부 체크하고  점 표시
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
                                val newestDate = safeParseDate(newestTime)
                                val lastSeenDate = safeParseDate(lastSeenTime)


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

    private fun safeParseDate(dateStr: String): Date? {
        val cleaned = dateStr.replace("\\s+".toRegex(), " ").trim()

        val possibleFormats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",  // ISO 8601
            "yyyy-MM-dd HH:mm:ss",           // 일반적인 DB 저장형
            "yyyy-MM-dd'T'HH:mm:ss",         // ISO에서 milliseconds 없는 경우
            "yyyy-MM-dd"                     // 날짜만 오는 경우까지 대비
        )

        for (format in possibleFormats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                return sdf.parse(cleaned)
            } catch (e: Exception) {
                // 무시하고 다음 포맷 시도
            }
        }

        return null // 아무 포맷도 맞지 않으면 null 반환
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
