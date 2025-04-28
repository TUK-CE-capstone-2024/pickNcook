package com.example.pickandcook.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

/**
 * Retrofit을 이용하여 OpenAI API와 통신할 준비를 하는 객체 설정 파일 (새로 추가 됨)
 */
object ApiClient {

    // OkHttpClient 설정: 타임아웃 60초로 설정
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)    // 연결 대기 시간
        .readTimeout(60, TimeUnit.SECONDS)       // 응답 대기 시간
        .writeTimeout(60, TimeUnit.SECONDS)      // 요청 전송 대기 시간
        .build()

    // Retrofit 인스턴스 생성
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/") // API 기본 주소
        .client(okHttpClient) // OkHttpClient 붙이기
        .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기
        .build()

    // 실제 API 인터페이스 객체 생성
    val openAIApi: OpenAIApi = retrofit.create(OpenAIApi::class.java)
}
