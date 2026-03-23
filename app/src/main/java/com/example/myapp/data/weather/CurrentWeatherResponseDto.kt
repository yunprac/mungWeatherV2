package com.example.myapp.data.weather

data class CurrentWeatherResponseDto(
    val weather: List<WeatherDto>,
    val main: MainDto
)

data class WeatherDto(
    val id: Int,
    val main: String,
    val description: String
)

data class MainDto(
    val temp: Double
)