package com.yoon.weatherapp.data.repository

import androidx.annotation.Keep
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import android.net.Uri

@Keep
@IgnoreExtraProperties
data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val name: String? = null,
    val breed: String? = null,
    val imageUri: String? = null,
    val isDefaultImage: Boolean = true
)

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firestorage: FirebaseStorage
) {
    private val USERS_COLLECTION = "users"
    private val PROFILE_IMAGES_PATH = "profile_images"
    private val DEFAULT_IMAGE_PREFIX = "default:"

    suspend fun createUserProfile(uid: String, email: String): Result<Unit> {
        return try {
            val userProfile = UserProfile(
                uid = uid,
                email = email,
                name = null,
                breed = null,
                imageUri = null,
                isDefaultImage = true
            )

            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .set(userProfile)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getUserProfile(uid: String): Result<UserProfile?> {
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            Result.success(snapshot.toObject(UserProfile::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(uid: String, imageUri: Uri): Result<String> {
        return try {
            val ref = firestorage.reference
                .child("$PROFILE_IMAGES_PATH/$uid.jpg")

            ref.putFile(imageUri).await()
            val downloadUrl = ref.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveProfile(
        uid: String,
        name: String,
        breed: String,
        imageUri: Uri?,
        isDefaultImage: Boolean
    ): Result<Unit> {
        return try {
            val currentProfile = getUserProfile(uid).getOrThrow()

            val imageUri = if (imageUri != null && !isDefaultImage) {
                uploadProfileImage(uid, imageUri).getOrThrow()
            } else if (isDefaultImage) {
                if (currentProfile?.isDefaultImage == false) {
                    deleteProfileImageIfExists(uid)
                }
                "$DEFAULT_IMAGE_PREFIX$breed"
            } else {
                currentProfile?.imageUri
            }

            val updates = mapOf(
                "name" to name,
                "breed" to breed,
                "imageUri" to imageUri,
                "isDefaultImage" to isDefaultImage
            )

            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        uid: String,
        name: String,
        breed: String,
        imageUri: Uri?,
        isDefaultImage: Boolean
    ): Result<Unit> {
        return saveProfile(
            uid = uid,
            name = name,
            breed = breed,
            imageUri = imageUri,
            isDefaultImage = isDefaultImage
        )
    }

    suspend fun deleteCurrentUserData(uid: String): Result<Unit> {
        return try {
            deleteProfileImageIfExists(uid)
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun deleteProfileImageIfExists(uid: String) {
        try {
            firestorage.reference
                .child("$PROFILE_IMAGES_PATH/$uid.jpg")
                .delete()
                .await()
        } catch (_: Exception) {
            // 파일이 없으면 무시
        }
    }
}
