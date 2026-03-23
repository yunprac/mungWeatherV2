package com.example.myapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import android.net.Uri

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
            val imageUri = if (imageUri != null && !isDefaultImage) {
                uploadProfileImage(uid, imageUri).getOrThrow()
            } else if (isDefaultImage) {
                "$DEFAULT_IMAGE_PREFIX$breed"
            } else {
                null
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
}
