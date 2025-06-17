package com.example.pickandcook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class MyinfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // fragment_myinfo.xml을 inflate해서 화면에 보여줌
        return inflater.inflate(R.layout.fragment_myinfo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // xml에 있는 버튼 ID로 뷰 가져오기
        val shoppingListBtn = view.findViewById<Button>(R.id.shoppingListBtn)
        val favoriteFoodsBtn = view.findViewById<Button>(R.id.btnFavoriteFoods)

        // 버튼 클릭 시 쇼핑리스트 Fragment로 전환
        shoppingListBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, ShoppingListFragment()) // mainFragmentContainer는 MainActivity의 FrameLayout ID
                .addToBackStack(null)  // 뒤로가기 가능하도록 스택에 추가
                .commit()
        }


        /*
        // 선호식품 목록 조회
        favoriteFoodsBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, FavoriteFoodsFragment())
                .addToBackStack(null)
                .commit()
        }

         */



    }
}
