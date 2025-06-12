package com.example.pickandcook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.pickandcook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 메인 화면 초기 실행 시 홈화면으로 설정
        if (savedInstanceState == null) {
            setFragment(HomeFragment())
            binding.menuNavi.selectedItemId = R.id.navi_home
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.menuNavi.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navi_recipe -> {
                    setFragment(SavedRecipeFragment())
                    true
                }
                R.id.navi_home -> {
                    setFragment(HomeFragment())
                    true
                }
                R.id.navi_myinfo -> {
                    val fragment = MyinfoFragment()
                    fragment.show(supportFragmentManager, fragment.tag)
                    true
                }
                else -> false
            }
        }
    }

    // Fragment 전환
    private fun setFragment(fragment: Fragment) {
        // 백스택 초기화
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainer, fragment)
            .commit()
    }
}
