package com.example.pickandcook.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.72.108:8080"//"http://10.0.2.2:8080"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) //json -> 객체 변환
            .build()

        retrofit.create(ApiService::class.java)
    }
}