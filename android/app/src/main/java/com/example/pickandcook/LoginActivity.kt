package com.example.pickandcook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pickandcook.api.LoginResponse
import com.example.pickandcook.api.RetrofitClient
import com.example.pickandcook.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val id = binding.etId.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = mapOf("id" to id, "password" to password)
            RetrofitClient.instance.loginUser(loginRequest)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            val userId = loginResponse?.userId
                            val userName = loginResponse?.userName

                            if (userId != null && userName != null) {
                                val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                sharedPref.edit().putString("userId", userId).putString("userName", userName).apply()

                                Toast.makeText(this@LoginActivity, loginResponse.message ?: "로그인 성공", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.putExtra("userName", userName)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "로그인 응답 오류", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val errorMsg = response.errorBody()?.string() ?: "로그인 실패"
                            Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // 회원가입 화면으로 이동
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}
