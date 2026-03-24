package com.example.myapp.home.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.ui.theme.Mung
import com.example.myapp.ui.theme.TextGray

@Composable
fun Policy(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "개인정보 처리방침",
                modifier = Modifier.align(Alignment.Center),
                fontFamily = Mung,
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = TextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                PolicySection(
                    title = "1. 수집하는 개인정보",
                    body = """
                        본 앱은 서비스 제공을 위해 다음과 같은 정보를 수집할 수 있습니다.
                        
                        1) 회원가입 및 로그인
                        - 이메일 주소
                        - 비밀번호 (이메일 로그인 시)
                        - Google 계정 정보 (이름, 이메일, 프로필 이미지 등)
                        
                        2) 사용자 프로필 정보
                        - 닉네임
                        - 프로필 이미지
                          사용자가 앨범에서 선택한 이미지 또는 앱에서 제공하는 기본 이미지
                        - 사용자 고유 식별자(uid)
                        
                        3) 위치 정보
                        - GPS 기반 현재 위치 정보 (실시간 조회)
                        
                        4) 기타 정보
                        - 기기 정보 및 서비스 이용 기록
                    """.trimIndent()
                )
            }

            item {
                PolicySection(
                    title = "2. 개인정보 이용 목적",
                    body = """
                        수집된 정보는 다음 목적을 위해 사용됩니다.
                        
                        - 회원 식별 및 로그인 기능 제공
                        - 사용자 프로필 관리 (닉네임, 프로필 이미지 설정)
                        - 현재 위치 기반 날씨 정보 제공
                        - 산책 추천 코디 및 시간 제공
                        - 생성형 AI를 활용한 추천 설명 문구 생성
                        - 서비스 운영, 유지보수 및 오류 개선
                    """.trimIndent()
                )
            }

            item {
                PolicySection(
                    title = "3. 개인정보의 처리 및 저장",
                    body = """
                        본 앱은 다음 서비스를 통해 개인정보를 처리합니다.
                        
                        - Firebase Authentication: 로그인 및 사용자 인증
                        - Cloud Firestore: 사용자 정보 저장
                          (닉네임, 이메일, uid, 프로필 이미지 여부, 이미지 URL)
                        - Cloud Storage for Firebase: 프로필 이미지 저장
                        
                        위 정보는 서비스 제공에 필요한 범위 내에서만 저장됩니다.
                    """.trimIndent()
                )
            }

            item {
                PolicySection(
                    title = "4. 제3자 제공 및 처리 위탁",
                    body = """
                        본 앱은 서비스 제공을 위해 아래 외부 서비스를 이용합니다.
                        
                        - Firebase (Google)
                        - Firebase AI Logic / Gemini API
                        - 외부 날씨 API (OpenWeatherthemap 등)
                        
                        이 과정에서 다음 정보가 외부 서비스로 전달될 수 있습니다.
                        
                        - 위치 정보 (날씨 조회 목적)
                        - 로그인 정보 (인증 처리 목적)
                        - AI 기능 사용 시 입력된 요청 내용
                        
                        단, 해당 정보는 각 기능 수행에 필요한 범위 내에서만 처리됩니다.
                    """.trimIndent()
                )
            }

            item {
                PolicySection(
                    title = "5. 위치정보 이용에 관한 사항",
                    body = """
                        - 위치 정보는 현재 위치 기반 서비스 제공을 위해 실시간으로 조회합니다.
                        - 위치 정보는 별도로 저장되지 않습니다.
                        - 이용자는 기기 설정을 통해 위치 권한을 허용하거나 거부할 수 있습니다.
                        - 위치 권한을 거부할 경우 일부 기능이 제한될 수 있습니다.
                    """.trimIndent()
                )
            }

            item {
                PolicySection(
                    title = "6. 생성형 AI 기능 관련 안내",
                    body = """
                        본 앱은 생성형 AI 기능을 통해 추천 설명 문구를 제공합니다.
                        
                        - AI 기능 사용 시 입력된 정보는 결과 생성을 위해 일시적으로 처리됩니다.
                        - 해당 입력값 및 생성 결과는 별도로 저장되지 않습니다.
                        - 이용자는 민감한 개인정보 입력을 자제해 주시기 바랍니다.
                    """.trimIndent()
                )
            }

            item {
                PolicySection(
                    title = "7. 개인정보 보관 기간",
                    body = """
                        - 회원 정보: 회원 탈퇴 시 즉시 삭제
                        - 프로필 이미지: 회원 탈퇴 시 삭제
                        - 위치 정보: 저장하지 않음 (실시간 조회만 수행)
                    """.trimIndent()
                )
            }

            item {
                PolicySection(
                    title = "8. 이용자의 권리",
                    body = """
                        이용자는 언제든지 다음 권리를 행사할 수 있습니다.
                        
                        - 개인정보 조회 및 수정
                        - 개인정보 삭제 요청
                        - 회원 탈퇴
                    """.trimIndent()
                )
            }

            item {
                PolicySection(
                    title = "9. 개인정보 보호 조치",
                    body = "본 앱은 개인정보 보호를 위해 합리적인 보안 조치를 적용하고 있습니다."
                )
            }

            item {
                PolicySection(
                    title = "10. 개인정보 처리방침의 변경",
                    body = "본 방침은 서비스 변경 또는 관련 법령에 따라 수정될 수 있으며, 변경 시 공지합니다."
                )
            }

            item {
                PolicySection(
                    title = "11. 계정 삭제 안내",
                    body = """
                        이용자는 앱 내 기능을 통해 언제든지 계정을 삭제할 수 있습니다.

                        1) 계정 삭제 방법
                        앱 내에서 아래 경로를 통해 계정 삭제가 가능합니다.
                        - 경로: 홈 > 프로필 > 회원탈퇴

                        2) 삭제되는 데이터
                        계정 삭제 시 다음 정보가 함께 삭제됩니다.
                        - 사용자 계정 정보 (이메일, uid)
                        - 프로필 정보 (닉네임, 프로필 이미지)
                        - 업로드된 이미지 파일 (Cloud Storage 저장 데이터)

                        3) 삭제 처리 시점
                        - 계정 삭제 요청 시 즉시 처리되며, 관련 데이터는 지체 없이 삭제됩니다.

                        4) 문의
                        계정 삭제와 관련하여 문제가 발생할 경우 아래 이메일로 문의해주시기 바랍니다.
                        - 이메일: leeyun002007@gmail.com
                    """.trimIndent()
                )
            }

            item {
                PolicySection(
                    title = "12. 문의처",
                    body = """
                        - 이메일: leeyun002007@gmail.com
                        - 시행일: 2026.03.24
                    """.trimIndent()
                )
            }
        }
    }
}

@Composable
private fun PolicySection(
    title: String,
    body: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontFamily = Mung,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFE0E0E0)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = body,
            fontFamily = Mung,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PolicyPreview() {
    Policy(onCloseClick = {})
}
