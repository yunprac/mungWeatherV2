package com.example.myapp.choice

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
import com.example.myapp.R
import com.example.myapp.ui.theme.Mung
import com.example.myapp.ui.theme.Orange
import com.example.myapp.ui.theme.TextGray

@Composable
fun ChoiceScreen(
    onNavigateToMain: () -> Unit,
    viewModel: ChoiceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val albumLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onAlbumImageSelected(uri)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    ChoiceContent(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onBreedChange = viewModel::onBreedChange,
        onProfileClick = viewModel::onProfileClick,
        onDismissDialog = viewModel::dismissProfileDialog,
        onBasicClick = viewModel::onBasicClick,
        onAlbumClick = {
            viewModel.onAlbumClick()
            albumLauncher.launch("image/*")
        },
        onCompleteClick = {
            viewModel.saveProfile(
                onSuccess = onNavigateToMain,
                onFail = {}
            )
        }
    )
}

@Composable
fun ChoiceContent(
    uiState: ChoiceUiState,
    onNameChange: (String) -> Unit,
    onBreedChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    onDismissDialog: () -> Unit,
    onBasicClick: () -> Unit,
    onAlbumClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    if (uiState.showProfileDialog) {
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
            ProfileImageSection(
                uiState = uiState,
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
            value = uiState.name,
            onValueChange = onNameChange,
            placeholder = { Text("댕댕이") },
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
                selectedBreed = uiState.breed == "small",
                imageRes = R.drawable.small,
                padding = 15.dp,
                onClick = { onBreedChange("small") }
            )

            BreedImageButton(
                breedValue = "middle",
                selectedBreed = uiState.breed == "middle",
                imageRes = R.drawable.middle,
                padding = 10.dp,
                onClick = { onBreedChange("middle") }
            )

            BreedImageButton(
                breedValue = "big",
                selectedBreed = uiState.breed == "big",
                imageRes = R.drawable.big,
                padding = 3.dp,
                onClick = { onBreedChange("big") }
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = onCompleteClick,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TextGray)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    text = "완료",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Mung
                )
            }
        }
    }
}

@Composable
private fun ProfileImageSection(
    uiState: ChoiceUiState,
    modifier: Modifier = Modifier
) {
    when (uiState.profileImageType) {
        ProfileImageType.ALBUM -> {
            AsyncImage(
                model = uiState.imageUri,
                contentDescription = "Album profile image",
                modifier = modifier.padding(10.dp),
                contentScale = ContentScale.Fit
            )
        }

        ProfileImageType.BASIC -> {
            val imageRes = when (uiState.breed) {
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

        ProfileImageType.NONE -> {
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
fun BreedImageButton(
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
fun ChoiceScreenPreview() {
    ChoiceContent(
        uiState = ChoiceUiState(),
        onNameChange = {},
        onBreedChange = {},
        onProfileClick = {},
        onDismissDialog = {},
        onBasicClick = {},
        onAlbumClick = {},
        onCompleteClick = {}
    )
}
