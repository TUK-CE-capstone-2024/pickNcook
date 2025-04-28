package com.example.pickandcook.api

data class ChatRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<Message>,
    val max_tokens: Int = 500
)

// GPT 요청 메시지 구성
data class Message(
    val role: String,
    val content: String
)

// GPT 응답 구조
data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)
