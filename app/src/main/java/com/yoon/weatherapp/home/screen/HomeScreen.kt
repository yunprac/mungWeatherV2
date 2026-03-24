package com.yoon.weatherapp.home.screen

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yoon.weatherapp.BuildConfig
import com.yoon.weatherapp.NavigationTarget
import com.yoon.weatherapp.home.viewModel.HomeViewModel
import com.yoon.weatherapp.ui.theme.Mung
import com.yoon.weatherapp.ui.theme.TextGray
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPolicy: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deletePassword by remember { mutableStateOf("") }
    var deleteErrorMessage by remember { mutableStateOf<String?>(null) }
    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()
    val googleDeleteRequest = remember {
        GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val pagerState = rememberPagerState(
        initialPage = uiState.currentPage,
        pageCount = {3}
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            viewModel.loadWeather()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
        if (viewModel.shouldRequestLocationPermission()) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiState.isProfilePanelOpen) {
        if (uiState.isProfilePanelOpen) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    LaunchedEffect(drawerState.isClosed) {
        if (drawerState.isClosed && uiState.isProfilePanelOpen) {
            viewModel.closeProfilePanel()
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.updateCurrentPage(page)
        }
    }

    LaunchedEffect(uiState.navigationTarget) {
        if (uiState.navigationTarget == NavigationTarget.Login) {
            onNavigateToLogin()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ProfileSheetContent(
                email = uiState.email,
                name = uiState.name,
                imageUrl = uiState.imageUrl,
                isLoading = uiState.isLoading,
                onUpdateProfileClick = onNavigateToEditProfile,
                onDeleteAccountClick = {
                    deletePassword = ""
                    deleteErrorMessage = null
                    showDeleteDialog = true
                },
                onLogoutClick = viewModel::logout,
                onPolicyClick = onNavigateToPolicy
            )
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                PageContent(
                    page = page,
                    uiState = uiState,
                    onProfileClick = viewModel::openProfilePanel,
                    onDogClick = viewModel::openDogDescriptionSheet,
                    onGenerateOutfitAiClick = viewModel::generateOutfitAiMessage,
                    onGenerateTimeAiClick = viewModel::generateTimeAiMessage
                )
            }

            DotIndicator(
                currentPage = uiState.currentPage,
                pageCount = 3,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }

    if (uiState.isDogDescriptionSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = viewModel::closeDogDescriptionSheet,
            sheetState = bottomSheetState
        ) {
            DogSheetContent(
                description = uiState.dogDescription
            )
        }
    }

    if (showDeleteDialog) {
        val isGoogleAccount = viewModel.isGoogleAccount()

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                deletePassword = ""
                deleteErrorMessage = null
            },
            title = {
                Text(
                    text = "계정 삭제",
                    fontFamily = Mung,
                    fontSize = 20.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "계정 삭제를 위해 본인인증을 진행해주세요.",
                        fontFamily = Mung,
                        color = TextGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isGoogleAccount) {
                        Text(
                            text = "구글 계정으로 다시 인증한 뒤 삭제할 수 있습니다.",
                            fontFamily = Mung,
                            color = TextGray
                        )
                    } else {
                        OutlinedTextField(
                            value = deletePassword,
                            onValueChange = {
                                deletePassword = it
                                deleteErrorMessage = null
                            },
                            label = { Text("비밀번호") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD32F2F)
                            ),
                            visualTransformation = PasswordVisualTransformation('*')
                        )
                    }

                    if (!deleteErrorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = deleteErrorMessage.orEmpty(),
                            color = Color(0xFFD32F2F),
                            fontSize = 13.sp
                        )
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        deletePassword = ""
                        deleteErrorMessage = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("취소")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isGoogleAccount) {
                            coroutineScope.launch {
                                runCatching {
                                    credentialManager.getCredential(
                                        context = context,
                                        request = googleDeleteRequest
                                    )
                                }.onSuccess { result ->
                                    val credential = result.credential

                                    if (
                                        credential is CustomCredential &&
                                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                    ) {
                                        val googleIdTokenCredential =
                                            GoogleIdTokenCredential.createFrom(credential.data)
                                        viewModel.deleteAccount(
                                            password = "",
                                            googleIdToken = googleIdTokenCredential.idToken,
                                            onSuccess = {
                                                showDeleteDialog = false
                                                deletePassword = ""
                                                deleteErrorMessage = null
                                            },
                                            onFailure = {
                                                deleteErrorMessage = "구글 계정 인증에 실패했습니다."
                                            }
                                        )
                                    } else {
                                        deleteErrorMessage = "구글 계정 인증에 실패했습니다."
                                    }
                                }.onFailure {
                                    deleteErrorMessage = "구글 계정 인증에 실패했습니다."
                                }
                            }
                        } else {
                            viewModel.deleteAccount(
                                password = deletePassword,
                                onSuccess = {
                                    showDeleteDialog = false
                                    deletePassword = ""
                                    deleteErrorMessage = null
                                },
                                onFailure = {
                                    deleteErrorMessage = "비밀번호가 일치하지 않습니다."
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("삭제")
                    }
                }
            }
        )
    }
}

@Composable
private fun DotIndicator(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) 10.dp else 8.dp)
                    .background(
                        color = if (index == currentPage) Color.White else Color.LightGray,
                        shape = CircleShape
                    )
            )
        }
    }
}
