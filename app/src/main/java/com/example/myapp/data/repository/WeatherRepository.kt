package com.example.myapp.data.repository

import android.content.Context
import android.location.Geocoder
import com.example.myapp.data.weather.OpenWeatherApi
import com.example.myapp.data.weather.WeatherInfo
import java.util.Locale
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: OpenWeatherApi
) {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        context: Context
    ): WeatherInfo {
        val response = api.getCurrentWeather(
            lat = lat,
            lon = lon,
            apiKey = apiKey
        )

        val weather = response.weather.firstOrNull()
        val regionName = getKoreanRegionName(
            context = context,
            lat = lat,
            lon = lon
        )

        return WeatherInfo(
            region = regionName,
            temperature = response.main.temp.toInt(),
            weatherStatus = weather?.description ?: "날씨 정보 없음"
        )
    }

    private fun getKoreanRegionName(
        context: Context,
        lat: Double,
        lon: Double
    ): String {
        return try {
            if (!Geocoder.isPresent()) {
                return "현재 위치"
            }

            val geocoder = Geocoder(context, Locale.KOREAN)
            val address = geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()

            listOf(
                address?.adminArea,
                address?.subAdminArea,
                address?.locality,
                address?.subLocality
            )
                .distinct()
                .filter { !it.isNullOrBlank() }
                .joinToString(separator = " ")
                .ifBlank { "현재 위치" }
        } catch (e: Exception) {
            "현재 위치"
        }
    }
}
