package com.yoon.weatherapp.signup

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yoon.weatherapp.NavigationTarget
import com.yoon.weatherapp.R
import com.yoon.weatherapp.ui.theme.Mung
import com.yoon.weatherapp.ui.theme.Orange
import com.yoon.weatherapp.ui.theme.TextGray

@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.navigationTarget) {
        if (uiState.navigationTarget == NavigationTarget.Login) {
            Toast.makeText(
                context,
                "회원가입에 성공했습니다.",
                Toast.LENGTH_SHORT
            ).show()
            onNavigateToLogin()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    SignupContent(
        uiState = uiState,
        onNavigateToLogin = onNavigateToLogin,
        onSignupClick = viewModel::signup,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onPasswordConfirmationChange = viewModel::onPasswordConfirmationChange,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onTogglePasswordConfirmationVisibility = viewModel::togglePasswordConfirmationVisibility
    )
}

@Composable
fun SignupContent(
    uiState: SignupUiState,
    onNavigateToLogin: () -> Unit,
    onSignupClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmationChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onTogglePasswordConfirmationVisibility: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_signup),
            contentDescription = "Signup background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = onNavigateToLogin,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextGray
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "앱 아이콘",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange)
            )

            Spacer(modifier = Modifier.height(17.dp))

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
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange),
                visualTransformation = if (uiState.passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation('*')
                },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (uiState.passwordVisible) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            },
                            contentDescription = "비밀번호 토글"
                        )
                    }
                }
            )

            Text(
                text = "영문 소문자, 숫자, 특수문자를 포함한 8자 이상으로 입력해주세요",
                fontFamily = Mung,
                color = Orange
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.passwordConfirmation,
                onValueChange = onPasswordConfirmationChange,
                label = { Text("비밀번호 확인") },
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = Mung,
                    fontSize = 18.sp
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Orange),
                visualTransformation = if (uiState.passwordConfirmationVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation('*')
                },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordConfirmationVisibility) {
                        Icon(
                            imageVector = if (uiState.passwordConfirmationVisible) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            },
                            contentDescription = "비밀번호 확인 토글"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onSignupClick,
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
                        text = "가입",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Mung
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SignupScreenPreview() {
    SignupContent(
        uiState = SignupUiState(),
        onNavigateToLogin = {},
        onSignupClick = {},
        onEmailChange = {},
        onPasswordChange = {},
        onPasswordConfirmationChange = {},
        onTogglePasswordVisibility = {},
        onTogglePasswordConfirmationVisibility = {}
    )
}
