package com.yoon.weatherapp.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yoon.weatherapp.BuildConfig
import com.yoon.weatherapp.NavigationTarget
import com.yoon.weatherapp.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.yoon.weatherapp.ui.theme.Mung
import com.yoon.weatherapp.ui.theme.TextGray
import com.yoon.weatherapp.ui.theme.Orange
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToChoice: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val credentialManager = remember { CredentialManager.create(context) }
    val coroutineScope = rememberCoroutineScope()
    val googleLoginRequest = remember {
        GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(BuildConfig.WEB_CLIENT_ID)
                    .build()
            )
            .build()
    }

    LaunchedEffect(uiState.navigationTarget) {
        when (uiState.navigationTarget) {
            NavigationTarget.Login -> {
                Toast.makeText(
                    context,
                    "로그인에 성공했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                if (uiState.firstLogin) {
                    onNavigateToChoice()
                } else {
                    onNavigateToMain()
                }
            }
            else -> Unit
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LoginContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onLoginClick = viewModel::login,
        onGoogleLoginClick = {
            coroutineScope.launch {
                runCatching {
                    credentialManager.getCredential(
                        context = context,
                        request = googleLoginRequest
                    )
                }.onSuccess { result ->
                    val credential = result.credential

                    if (
                        credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        viewModel.loginWithGoogle(googleIdTokenCredential.idToken)
                    } else {
                        Toast.makeText(
                            context,
                            "구글 로그인 자격 증명을 가져오지 못했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.onFailure {
                    Toast.makeText(
                        context,
                        "구글 로그인에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        },
        onNavigateToSignUp = onNavigateToSignUp
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = "배경 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = { Text("example@email.com") },
            label = { Text("이메일") },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = Mung,
                fontSize = 18.sp
            ),
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("비밀번호") },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = Mung,
                fontSize = 18.sp
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Orange
            ),
            visualTransformation = if (uiState.passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation('*')
            },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    val image = if (uiState.passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff
                    Icon(imageVector = image, contentDescription = "비밀번호 숨기기/보기")
                }
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier.height(50.dp).fillMaxWidth(),
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TextGray
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    text = "로그인",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Mung
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

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
                text = " 또는 ",
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

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onGoogleLoginClick,
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(1.dp, TextGray),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextGray
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "구글 로그인",
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Text(
            text = "구글 로그인",
            fontFamily = Mung,
            fontWeight = FontWeight.Bold,
            color = TextGray,
            modifier = Modifier.padding(top = 10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "계정이 없으신가요?",
                fontSize = 15.sp,
                fontFamily = Mung,
                color = TextGray
            )
            TextButton(onClick = onNavigateToSignUp) {
                Text(
                    text = "회원가입",
                    fontSize = 15.sp,
                    fontFamily = Mung,
                    color = Orange,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
@Preview
fun LoginScreenPreview() {
    LoginContent(
        uiState = LoginUiState(), // 빈 상태 객체
        onEmailChange = {},
        onPasswordChange = {},
        onTogglePasswordVisibility = {},
        onLoginClick = {},
        onGoogleLoginClick = {},
        onNavigateToSignUp = {}
    )
}
