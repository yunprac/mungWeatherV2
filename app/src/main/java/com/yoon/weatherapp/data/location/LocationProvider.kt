package com.yoon.weatherapp.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationProvider(
    context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location =
        suspendCancellableCoroutine { cont ->
            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
                .addOnSuccessListener { location ->
                    if (location != null) {
                        cont.resume(location)
                    } else {
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { lastLocation ->
                                if (lastLocation != null) {
                                    cont.resume(lastLocation)
                                } else {
                                    cont.resumeWithException(
                                        IllegalStateException("현재 위치를 가져올 수 없습니다. 위치 서비스를 확인해 주세요.")
                                    )
                                }
                            }
                            .addOnFailureListener { e ->
                                cont.resumeWithException(e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { lastLocation ->
                            if (lastLocation != null) {
                                cont.resume(lastLocation)
                            } else {
                                cont.resumeWithException(e)
                            }
                        }
                        .addOnFailureListener { fallbackError ->
                            cont.resumeWithException(fallbackError)
                        }
                }

            cont.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
}