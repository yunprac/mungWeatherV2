package com.yoon.weatherapp.home.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoon.weatherapp.R
import com.yoon.weatherapp.home.HomeUiState
import com.yoon.weatherapp.ui.theme.Clear
import com.yoon.weatherapp.ui.theme.GeminiBlue
import com.yoon.weatherapp.ui.theme.Mung
import com.yoon.weatherapp.ui.theme.TextBox
import com.yoon.weatherapp.ui.theme.TextGray

@Composable
fun PageContent(
    page: Int,
    uiState: HomeUiState,
    onProfileClick: () -> Unit,
    onDogClick: () -> Unit,
    onGenerateOutfitAiClick: () -> Unit,
    onGenerateTimeAiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Clear)
    ) {
        IconButton(
            onClick = onProfileClick,
            modifier = modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
                .size(50.dp)
        ) {
            Icon(
                modifier = modifier.fillMaxSize(),
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "프로필",
                tint = Color.White
            )
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = uiState.locationText,
                fontFamily = Mung,
                fontSize = 23.sp
            )

            Row(
                modifier = modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = modifier.padding(end = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "추천 코디",
                        fontFamily = Mung,
                        fontSize = 23.sp,
                        color = TextGray
                    )

                    Text(
                        text = uiState.outfit,
                        fontFamily = Mung,
                        fontSize = 28.sp
                    )
                }

                Text(
                    text = uiState.temperatureText,
                    fontFamily = Mung,
                    fontSize = 70.sp,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = modifier.padding(start = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "추천 시간",
                        fontFamily = Mung,
                        fontSize = 23.sp,
                        color = TextGray
                    )

                    Text(
                        text = uiState.time,
                        fontFamily = Mung,
                        fontSize = 28.sp
                    )
                }
            }

            Text(
                text = uiState.weatherStatusText,
                fontFamily = Mung,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(80.dp))

            when (page) {
                0 -> {
                    AiMessageSection(
                        buttonText = "AI 생성",
                        message = uiState.outfitAiMessage,
                        isLoading = uiState.isOutfitAiLoading,
                        onClick = onGenerateOutfitAiClick
                    )
                }

                1 -> {
                    uiState.dogImageRes?.let { imageRes ->
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = "강아지 이미지",
                            modifier = modifier
                                .size(450.dp)
                                .clickable { onDogClick() }
                        )
                    }
                }

                2 -> {
                    AiMessageSection(
                        buttonText = "AI 생성",
                        message = uiState.timeAiMessage,
                        isLoading = uiState.isTimeAiLoading,
                        onClick = onGenerateTimeAiClick
                    )
                }
            }
        }
    }
}

@Composable
private fun AiMessageSection(
    buttonText: String,
    message: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    strokeWidth = 3.dp,
                    color = Color.White
                )
            }

            message.isBlank() -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedButton(
                        onClick = onClick,
                        modifier = Modifier.size(70.dp),
                        shape = CircleShape,
                        border = BorderStroke(2.dp, GeminiBlue),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextGray
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_gemini),
                            contentDescription = buttonText,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "AI 코멘트 생성",
                        fontFamily = Mung,
                        fontSize = 22.sp,
                        color = TextGray
                    )
                }
            }

            else -> {
                RoundedTextBox(text = message)
            }
        }
    }
}

@Composable
fun RoundedTextBox(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(20.dp))
            .background(TextBox)
            .padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = Mung,
            fontSize = 25.sp
        )
    }
}

private fun previewState() = HomeUiState(
    email = "test@example.com",
    name = "멍집사",
    imageUrl = null,
    temperatureText = "22",
    locationText = "경기도 일산",
    weatherStatusText = "맑음",
    dogImageRes = R.drawable.big,
    dogDescription = "활동량이 많고 산책을 좋아하는 견종입니다.",
    outfit = "패딩",
    time = "1시간",
    outfitAiMessage = "바람이 차갑고 기온이 낮아서 패딩이 체온 유지에 도움이 됩니다.\n\n대신 얇은 맨투맨에 하네스를 함께 입히는 조합도 무난합니다.",
    timeAiMessage = "기온이 무난해서 1시간 정도 천천히 걷기 좋습니다.\n\n산책 중에는 체온 변화를 봐 가면서 중간에 물을 챙겨 주세요."
)

@Preview(showBackground = true)
@Composable
private fun PageContentPage0Preview() {
    PageContent(
        page = 0,
        uiState = previewState(),
        onProfileClick = {},
        onDogClick = {},
        onGenerateOutfitAiClick = {},
        onGenerateTimeAiClick = {}
    )
}
