package com.example.pick

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.pick.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 아이디 중복 확인
        binding.btnCheck.setOnClickListener {
            val id = binding.etId.text.toString().trim() // 공백 제거

            if (id.isEmpty()) {
                binding.tvIdErr.text = "아이디를 입력하세요."
                binding.tvIdErr.setTextColor(Color.RED)
                binding.tvIdErr.visibility = View.VISIBLE
            } else if (id == "test") {
                binding.tvIdErr.text = "사용할 수 없는 아이디입니다."
                binding.tvIdErr.setTextColor(Color.RED)
                binding.tvIdErr.visibility = View.VISIBLE
            } else {
                binding.tvIdErr.text = "사용 가능한 아이디입니다."
                binding.tvIdErr.setTextColor(Color.BLUE)
                binding.tvIdErr.visibility = View.VISIBLE
            }
        }
        // 비밀번호 확인 검사
        binding.etPWCheck.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = binding.etPW.text.toString()
                val confirmPassword = s.toString()

                if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                    binding.tvPWErr.text = "비밀번호가 일치하지 않습니다."
                    binding.tvPWErr.setTextColor(Color.RED)
                    binding.tvPWErr.visibility = View.VISIBLE
                } else {
                    binding.tvPWErr.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
