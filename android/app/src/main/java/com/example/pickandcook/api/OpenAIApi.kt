package com.example.pickandcook.api

import retrofit2.Call
import retrofit2.http.*


interface OpenAIApi {
    @POST("v1/chat/completions")
    fun getChatCompletion(
        @Header("Authorization") auth: String,  // 인증 토큰
        @Body request: ChatRequest              // 요청 본문
    ): Call<ChatResponse>
}