package com.yoon.weatherapp.data.weather

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CurrentWeatherResponseDto(
    @SerializedName("weather")
    val weather: List<WeatherDto>,
    val main: MainDto
)

@Keep
data class WeatherDto(
    @SerializedName("id")
    val id: Int,
    val main: String,
    val description: String
)

@Keep
data class MainDto(
    @SerializedName("temp")
    val temp: Double
)