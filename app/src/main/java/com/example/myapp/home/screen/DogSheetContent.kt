package com.example.myapp.home.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.ui.theme.Mung
import com.example.myapp.ui.theme.TextGray

@Composable
fun DogSheetContent(
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "견종 설명",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontFamily = Mung,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = description,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontFamily = Mung,
            fontSize = 22.sp,
            color = TextGray
        )

        Spacer(modifier = modifier.height(20.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun DogSheetContentPreview() {
    DogSheetContent(
        description = "견종 설명"
    )
}
