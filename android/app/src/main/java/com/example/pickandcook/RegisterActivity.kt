package com.example.pickandcook

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pickandcook.api.RegisterResponse
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.api.User
import com.example.pickandcook.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
            val id = binding.etId.text.toString().trim()
            val password = binding.etPW.text.toString().trim()
            val name = binding.etName.text.toString().trim()
            val intent = Intent(this, LoginActivity::class.java)

            if (id.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(id = id, password = password, userName = name)

            RetrofitClient.instance.registerUser(user)
                .enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if (response.isSuccessful) {
                            val registerResponse = response.body()
                            Toast.makeText(
                                this@RegisterActivity,
                                registerResponse?.message ?: "회원가입 성공",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(intent)
                            finish()

                        } else {
                            val errorMsg = response.errorBody()?.string() ?: "회원가입 실패"
                            Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Toast.makeText(this@RegisterActivity, "오류: ${t.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }
    }
}
