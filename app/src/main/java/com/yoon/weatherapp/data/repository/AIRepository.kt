package com.yoon.weatherapp.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import javax.inject.Inject

class AIRepository @Inject constructor() {

    private val model = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel("gemini-2.5-flash-lite")

    suspend fun generateOutfitMessage(
        breed: String,
        temperature: Int,
        weatherStatus: String,
        outfit: String
    ): Result<String> {
        val prompt = """
            Write the answer in Korean.
            You are explaining a dog walking outfit recommendation for a mobile app.

            Dog breed: $breed
            Temperature: $temperature C
            Weather: $weatherStatus
            Recommended outfit: $outfit

            Rules:
            1. Write exactly 2 short paragraphs.
            2. Paragraph 1 explains why this outfit is recommended.
            3. Paragraph 2 gives one specific alternative outfit example.
            4. Keep each paragraph to 3 or 4 sentences.
            5. No title, no numbering, no bullet points, no emoji.
        """.trimIndent()

        return generateText(prompt)
    }

    suspend fun generateTimeMessage(
        breed: String,
        temperature: Int,
        weatherStatus: String,
        time: String
    ): Result<String> {
        val prompt = """
            Write the answer in Korean.
            You are explaining a dog walking time recommendation for a mobile app.

            Dog breed: $breed
            Temperature: $temperature C
            Weather: $weatherStatus
            Recommended walk time: $time

            Rules:
            1. Write exactly 2 short paragraphs.
            2. Paragraph 1 explains why this walk time is recommended.
            3. Paragraph 2 explains what to watch out for in this weather.
            4. Keep each paragraph to 3 or 4 sentences.
            5. No title, no numbering, no bullet points, no emoji.
        """.trimIndent()

        return generateText(prompt)
    }

    private suspend fun generateText(prompt: String): Result<String> {
        return try {
            val response = model.generateContent(prompt)
            Result.success(
                response.text
                    ?.replace("\r\n", "\n")
                    ?.trim()
                    .orEmpty()
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
