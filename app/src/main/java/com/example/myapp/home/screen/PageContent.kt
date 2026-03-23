package com.example.myapp.home.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.R
import com.example.myapp.home.HomeUiState
import com.example.myapp.ui.theme.Clear
import com.example.myapp.ui.theme.GeminiBlue
import com.example.myapp.ui.theme.Mung
import com.example.myapp.ui.theme.TextBox
import com.example.myapp.ui.theme.TextGray

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

            Spacer(modifier = Modifier.height(100.dp))

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
                            contentDescription = "견종 이미지",
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_gemini),
                    contentDescription = "AI 생성",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (message.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            RoundedTextBox(text = message)
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
            fontSize = 23.sp
        )
    }
}

private fun previewState() = HomeUiState(
    email = "test@example.com",
    name = "멍집사",
    imageUrl = null,
    temperatureText = "22도",
    locationText = "경기도 안산시",
    weatherStatusText = "맑음",
    dogImageRes = R.drawable.big,
    dogDescription = "활동량이 많고 산책을 좋아하는 견종입니다.",
    outfit = "후드티",
    time = "1시간",
    outfitAiMessage = "바람이 살짝 있어 후드티가 체온 유지에 도움이 됩니다.\n\n대안으로는 얇은 맨투맨에 하네스를 함께 입히는 조합도 무난합니다.",
    timeAiMessage = "기온이 무난해서 1시간 정도 천천히 걷기 좋습니다.\n\n한낮에는 체온이 오를 수 있으니 중간에 물을 챙겨 주세요."
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
