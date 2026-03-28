# 🐶 mungWeatherV2
> **반려견 보호자를 위한 맞춤형 날씨 & 산책 코디 추천 앱 (Kotlin 버전)**

## 🌤️ 프로젝트 개요
**mungWeatherV2**는 원래의 mungWeather를 Kotlin과 Jetpack Compose로 현대화한 버전입니다.  
반려견 보호자를 위한 스마트 날씨 앱으로,  
사용자의 위치 기반 **현재 날씨**를 조회하고,  
**견종별 맞춤 산책 시간과 의상**을 추천하고 AI가 추천 이유에 대해 설명해주는 서비스입니다.

## ✨ 주요 기능
| 기능 | 설명 |
|:---|:---|
| 🐾 **회원가입 / 로그인** | Firebase Authentication & Realtime Database를 이용한 사용자 관리 (이메일, 비밀번호, 이름, 견종 등) |
| ☀️ **날씨 정보 제공** | OpenWeatherMap API를 이용해 현재 위치 기반의 날씨 데이터를 표시 |
| 👕 **맞춤 산책/코디 추천** | 견종 + 온도 조건에 따라 최적의 산책 시간 & 의상 제안 |
| 🎨 **UI/UX 구성** | Jetpack Compose 기반 현대적 UI, 부드러운 애니메이션, 반응형 디자인 |

## 🧩 주요 클래스 구성

| 클래스/패키지 | 역할 |
|:---|:---|
| `login` | Firebase 로그인 처리 및 사용자 인증 |
| `signup` | 회원가입 및 사용자 정보 DB 저장 |
| `choice` | 견종 선택 및 관리 |
| `main` | 메인 화면. 날씨 조회, 추천 표시, 프로필 관리 |
| `data` | 공유 데이터 액세스 레이어 |
| `di` | 의존성 주입 설정 |
| `ui/theme` | Compose 테마 설정 |

## 🛠️ 사용 기술 및 라이브러리

**언어:** Kotlin (100%)  
**아키텍처:** MVVM, Jetpack Compose  
**주요 라이브러리 및 API:**
- 🔹 Jetpack Compose, Material 3
- 🔹 Firebase Auth, Firebase Realtime Database  
- 🔹 OpenWeatherMap API (Retrofit2 + GsonConverterFactory)  
- 🔹 Google Play Location Services
- 🔹 Coroutines, Flow
- 🔹 Hilt (의존성 주입)

---

## 📝 버전 정보
- **V1 (Java)**: 원본 프로젝트 - [mungWeather](https://github.com/yunprac/mungWeather)
- **V2 (Kotlin + Compose)**: 현대화 버전 - 현재 프로젝트
