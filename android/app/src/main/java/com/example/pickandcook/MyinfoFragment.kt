package com.example.pickandcook

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.api.User
import com.example.pickandcook.databinding.FragmentMyinfoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyinfoFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMyinfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true  // 중간 상태 생략하고 바로 확장
            }
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyinfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId != null) {
            RetrofitClient.instance.getUserInfo(userId)
                .enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        Log.d("UserAPI", "status: ${response.code()}, body: ${response.body()}, error: ${response.errorBody()?.string()}")

                        if (response.isSuccessful) {
                            val user = response.body()
                            binding.userName.text = user?.userName ?: "이름 없음"
                        } else {
                            binding.userName.text = "불러오기 실패"
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        binding.userName.text = "네트워크 오류"
                    }
                })
        } else {
            binding.userName.text = "로그인이 필요합니다"
        }


        binding.closeBtn.setOnClickListener {
            dismiss()
        }

        binding.preferFoodBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, PreferFoodFragment())
                .addToBackStack(null)
                .commit()
            dismiss()
        }

        binding.shoppingListBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, ShoppingListFragment()) // mainFragmentContainer는 MainActivity의 FrameLayout ID
                .addToBackStack(null)  // 뒤로가기 가능하도록 스택에 추가
                .commit()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
