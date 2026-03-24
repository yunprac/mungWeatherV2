package com.yoon.weatherapp.home.viewModel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yoon.weatherapp.R

data class DogStageResource(
    @DrawableRes val imageRes: Int,
    @StringRes val outfitRes: Int,
    @StringRes val timeRes: Int
)

object DogWeatherResources {

    private val bigResource = mapOf(
        0 to DogStageResource(R.drawable.big_0, R.string.big_outfit_0, R.string.big_time_0),
        1 to DogStageResource(R.drawable.big_1, R.string.big_outfit_1, R.string.big_time_1),
        2 to DogStageResource(R.drawable.big, R.string.big_outfit_2, R.string.big_time_2),
        3 to DogStageResource(R.drawable.big, R.string.big_outfit_3, R.string.big_time_3),
        4 to DogStageResource(R.drawable.big, R.string.big_outfit_4, R.string.big_time_4)
    )

    private val middleResource = mapOf(
        0 to DogStageResource(R.drawable.middle_0, R.string.middle_outfit_0, R.string.middle_time_0),
        1 to DogStageResource(R.drawable.middle_1, R.string.middle_outfit_1, R.string.middle_time_1),
        2 to DogStageResource(R.drawable.middle_2, R.string.middle_outfit_2, R.string.middle_time_2),
        3 to DogStageResource(R.drawable.middle_3, R.string.middle_outfit_3, R.string.middle_time_3),
        4 to DogStageResource(R.drawable.middle, R.string.middle_outfit_4, R.string.middle_time_4)
    )

    private val smallResource = mapOf(
        0 to DogStageResource(R.drawable.small_0, R.string.small_outfit_0, R.string.small_time_0),
        1 to DogStageResource(R.drawable.small_1, R.string.small_outfit_1, R.string.small_time_1),
        2 to DogStageResource(R.drawable.small_2, R.string.small_outfit_2, R.string.small_time_2),
        3 to DogStageResource(R.drawable.small_3, R.string.small_outfit_3, R.string.small_time_3),
        4 to DogStageResource(R.drawable.small_4, R.string.small_outfit_4, R.string.small_time_4)
    )

    fun getResource(breed: String, stage: Int): DogStageResource {
        return when (breed) {
            "big" -> bigResource[stage]
            "middle" -> middleResource[stage]
            "small" -> smallResource[stage]
            else -> null
        } ?: smallResource[0]!!
    }

    @StringRes
    fun getBreedDescriptionRes(breed: String): Int {
        return breedDescriptionMap[breed] ?: 0
    }

    private val breedDescriptionMap = mapOf(
        "big" to R.string.big_description,
        "middle" to R.string.middle_description,
        "small" to R.string.small_description
    )
}