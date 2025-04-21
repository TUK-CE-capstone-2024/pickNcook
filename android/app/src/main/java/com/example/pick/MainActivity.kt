package com.example.pick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.pick.databinding.ActivityMainBinding

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
            val selectedFragment = when (item.itemId) {
                R.id.navi_recipe -> SavedRecipeFragment()
                R.id.navi_home -> HomeFragment()
                R.id.navi_myinfo -> MyinfoFragment()
                else -> null
            }

            selectedFragment?.let { setFragment(it) }
            selectedFragment != null
        }
    }
    // Fragment 전환
    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainer, fragment)
            .commit()
    }
}
