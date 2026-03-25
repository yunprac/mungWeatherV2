package com.yoon.weatherapp.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yoon.weatherapp.R
import com.yoon.weatherapp.ui.theme.Mung
import com.yoon.weatherapp.ui.theme.Orange
import com.yoon.weatherapp.ui.theme.TextGray

@Composable
fun ProfileSheetContent(
    email: String,
    name: String,
    imageUrl: String?,
    isLoading: Boolean,
    onUpdateProfileClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onPolicyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.fillMaxWidth(0.75f)
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.bg_signup),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(24.dp).align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                val defaultImageRes = when (imageUrl) {
                    "default:middle" -> R.drawable.middle
                    "default:big" -> R.drawable.big
                    "default:small" -> R.drawable.small
                    else -> R.drawable.foot
                }

                if (imageUrl.isNullOrBlank() || imageUrl.startsWith("default:")) {
                    Image(
                        painter = painterResource(id = defaultImageRes),
                        contentDescription = "Profile image",
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color.White, CircleShape)
                            .border(2.5.dp, Orange, CircleShape)
                            .padding(15.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Profile image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White, CircleShape)
                            .border(2.5.dp, Orange, CircleShape),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = email,
                    fontFamily = Mung,
                    color = TextGray,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = name,
                    fontFamily = Mung,
                    color = TextGray,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(50.dp))

                Button(
                    onClick = onUpdateProfileClick,
                    modifier = Modifier
                        .height(35.dp)
                        .fillMaxWidth(0.7f),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextGray
                    )
                ) {
                    Text(
                        text = "회원 정보 수정",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Mung,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = onLogoutClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .height(35.dp)
                        .fillMaxWidth(0.55f),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "로그아웃",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Mung,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Button(
                onClick = onDeleteAccountClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp)
                    .height(35.dp)
                    .fillMaxWidth(0.5f),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                )
            ) {
                Text(
                    text = "계정 삭제",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Mung,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

            TextButton(
                onClick = onPolicyClick,
                modifier = modifier
                    .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = "개인정보 처리방침",
                    fontSize = 12.sp,
                    fontFamily = Mung,
                    color = TextGray,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileSheetContentPreview() {
    ProfileSheetContent(
        email = "email",
        name = "name",
        isLoading = false,
        onUpdateProfileClick = {},
        onDeleteAccountClick = {},
        onLogoutClick = {},
        onPolicyClick = {},
        imageUrl = null
    )
}
