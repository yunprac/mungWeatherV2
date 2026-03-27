package com.yoon.weatherapp.home.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.yoon.weatherapp.R
import com.yoon.weatherapp.choice.ProfileImageType
import com.yoon.weatherapp.home.viewModel.HomeViewModel
import com.yoon.weatherapp.ui.theme.Mung
import com.yoon.weatherapp.ui.theme.Orange
import com.yoon.weatherapp.ui.theme.TextGray

@Composable
fun EditProfileScreen(
    onCancelClick: () -> Unit,
    onCompleteClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var breed by rememberSaveable { mutableStateOf("") }
    var showProfileDialog by rememberSaveable { mutableStateOf(false) }
    var profileImageType by rememberSaveable { mutableStateOf(ProfileImageType.NONE) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentImageUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var isInitialized by rememberSaveable { mutableStateOf(false) }

    val albumLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            profileImageType = ProfileImageType.ALBUM
            currentImageUrl = null
        }
    }

    LaunchedEffect(uiState.name, uiState.breed, uiState.imageUrl, uiState.isDefaultImage) {
        if (!isInitialized) {
            name = uiState.name
            breed = uiState.breed
            currentImageUrl = uiState.imageUrl
            profileImageType = when {
                uiState.isDefaultImage -> ProfileImageType.BASIC
                !uiState.imageUrl.isNullOrBlank() -> ProfileImageType.ALBUM
                else -> ProfileImageType.NONE
            }
            isInitialized = true
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    EditProfileContent(
        name = name,
        breed = breed,
        currentImageUrl = currentImageUrl,
        selectedImageUri = selectedImageUri,
        profileImageType = profileImageType,
        showProfileDialog = showProfileDialog,
        isLoading = uiState.isLoading,
        onNameChange = { name = it },
        onBreedChange = { breed = it },
        onProfileClick = { showProfileDialog = true },
        onDismissDialog = { showProfileDialog = false },
        onBasicClick = {
            selectedImageUri = null
            currentImageUrl = null
            profileImageType = ProfileImageType.BASIC
            showProfileDialog = false
        },
        onAlbumClick = {
            showProfileDialog = false
            albumLauncher.launch("image/*")
        },
        onCompleteClick = {
            if (name.trim().isBlank()) {
                Toast.makeText(context, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@EditProfileContent
            }

            if (breed.trim().isBlank()) {
                Toast.makeText(context, "견종을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@EditProfileContent
            }

            if (profileImageType == ProfileImageType.NONE) {
                Toast.makeText(context, "프로필 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@EditProfileContent
            }

            viewModel.updateProfile(
                name = name,
                breed = breed,
                imageUri = selectedImageUri,
                isDefaultImage = profileImageType == ProfileImageType.BASIC,
                onSuccess = onCompleteClick,
                onFailure = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
        },
        onCancelClick = onCancelClick
    )
}

@Composable
private fun EditProfileContent(
    name: String,
    breed: String,
    currentImageUrl: String?,
    selectedImageUri: Uri?,
    profileImageType: ProfileImageType,
    showProfileDialog: Boolean,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onBreedChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onBasicClick: () -> Unit,
    onAlbumClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text("프로필 이미지 선택") },
            text = { Text("기본 이미지를 사용하거나 앨범에서 선택해주세요.") },
            confirmButton = {
                TextButton(onClick = onBasicClick) {
                    Text("기본 이미지")
                }
            },
            dismissButton = {
                TextButton(onClick = onAlbumClick) {
                    Text("앨범에서 선택")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            EditProfileImageSection(
                breed = breed,
                currentImageUrl = currentImageUrl,
                selectedImageUri = selectedImageUri,
                profileImageType = profileImageType,
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, Orange, CircleShape)
                    .clickable(onClick = onProfileClick)
            )

            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                tonalElevation = 3.dp,
                shadowElevation = 3.dp
            ) {
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change profile image"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("이름") },
            label = { Text("이름") },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = Mung,
                fontSize = 18.sp
            ),
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange)
        )

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = TextGray
            )

            Text(
                text = " 견종 선택 ",
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 14.sp,
                color = TextGray,
                fontFamily = Mung
            )

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = TextGray
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BreedImageButton(
                breedValue = "small",
                selectedBreed = breed == "small",
                imageRes = R.drawable.small,
                padding = 15.dp,
                onClick = { onBreedChange("small") }
            )

            BreedImageButton(
                breedValue = "middle",
                selectedBreed = breed == "middle",
                imageRes = R.drawable.middle,
                padding = 10.dp,
                onClick = { onBreedChange("middle") }
            )

            BreedImageButton(
                breedValue = "big",
                selectedBreed = breed == "big",
                imageRes = R.drawable.big,
                padding = 3.dp,
                onClick = { onBreedChange("big") }
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancelClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(
                    text = "취소",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Mung
                )
            }

            Button(
                onClick = onCompleteClick,
                enabled = !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TextGray)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "완료",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Mung
                    )
                }
            }
        }
    }
}

@Composable
private fun EditProfileImageSection(
    breed: String,
    currentImageUrl: String?,
    selectedImageUri: Uri?,
    profileImageType: ProfileImageType,
    modifier: Modifier = Modifier
) {
    when {
        profileImageType == ProfileImageType.ALBUM && selectedImageUri != null -> {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Album profile image",
                modifier = modifier,
                contentScale = ContentScale.Fit
            )
        }

        profileImageType == ProfileImageType.ALBUM && !currentImageUrl.isNullOrBlank() -> {
            AsyncImage(
                model = currentImageUrl,
                contentDescription = "Current profile image",
                modifier = modifier,
                contentScale = ContentScale.Fit
            )
        }

        profileImageType == ProfileImageType.BASIC -> {
            val imageRes = when (breed) {
                "small" -> R.drawable.small
                "middle" -> R.drawable.middle
                "big" -> R.drawable.big
                else -> R.drawable.small
            }

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Default profile image",
                modifier = modifier,
                contentScale = ContentScale.Fit
            )
        }

        else -> {
            Image(
                painter = painterResource(id = R.drawable.foot),
                contentDescription = "Placeholder profile image",
                modifier = modifier.padding(15.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun BreedImageButton(
    breedValue: String,
    selectedBreed: Boolean,
    imageRes: Int,
    padding: Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selectedBreed) Color.LightGray else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = breedValue,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    EditProfileContent(
        name = "name",
        breed = "small",
        currentImageUrl = null,
        selectedImageUri = null,
        profileImageType = ProfileImageType.BASIC,
        showProfileDialog = false,
        isLoading = false,
        onNameChange = {},
        onBreedChange = {},
        onProfileClick = {},
        onDismissDialog = {},
        onBasicClick = {},
        onAlbumClick = {},
        onCompleteClick = {},
        onCancelClick = {}
    )
}
