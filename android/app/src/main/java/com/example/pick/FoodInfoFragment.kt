package com.example.pick

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pick.databinding.FragmentFoodInfoBinding

class FoodInfoFragment : Fragment() {

    private var _binding: FragmentFoodInfoBinding? = null
    private val binding get() = _binding!!

    private var isFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString("name") ?: ""
        val imageResId = arguments?.getInt("imageResId") ?: R.drawable.ic_placeholder

        binding.foodName.text = name
        binding.foodImage.setImageResource(imageResId)
        // 식재료 정보들도 가져와야함 ( ***** )

        // 하트 클릭 시 ( ***** 선호식품)
        binding.btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_heart_red
                else R.drawable.ic_heart_black
            )
        }

        // 닫기 버튼
        binding.btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(name: String, imageResId: Int): FoodInfoFragment {
            val fragment = FoodInfoFragment()
            fragment.arguments = Bundle().apply {
                putString("name", name)
                putInt("imageResId", imageResId)
            }
            return fragment
        }
    }
}
