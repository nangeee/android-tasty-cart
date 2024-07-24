# 🛒 android-tasty-cart


## 💡 Project Introduction
Tasty Cart는 사용자가 식료품과 재료를 구매할 수 있도록 돕는 안드로이드 앱입니다. 또한 장바구니에 담은 재료를 기반으로 레시피를 제안해줍니다.


## 🪄 Development Timeline
2024.06.22-2024.06.23


## 🗝️ Technology Stack
**IDE**: Android Studio

**Programming Language**: Kotlin

**Backend**: Firebase

**API**: Edamam Recipe Search API


## 🎯 Functions
1. **로그인 및 회원가입**
   - 사용자 계정 생성 및 로그인 가능
   - Firebase Authentication 활용: Google 계정으로 회원가입 및 로그인
   - 이메일/비밀번호 유효성 검증
2. **식료품 구매**
   - 식료품 목록 확인 및 구매 (결제 기능 x)
   - Firebase Realtime Database 데이터 연동
3. **장바구니 기능**
   - 식료품 장바구니에 담기
   - 장바구니에서 본인이 담은 식료품 확인 및 수량 수정 가능
   - Firebase Realtime Database에 실시간으로 데이터 동기화
4. **레시피 추천 기능**
   - 장바구니에 담은 식료품을 기반으로 레시피 추천
   - Edamam API를 활용하여 실시간 레시피 검색
   - 레시피 제공 사이트 접속하여 상세정보 확인
5. **레시피 북마크 기능**
   - 마음에 드는 레시피를 북마크에 저장
   - Firebase Realtime Database에 실시간으로 데이터 동기화


## 🧀 Implementation Challenges
- 짧은 개발 기간 (약 24시간)
- Edamam API를 통합하여 정확한 레시피 추천을 제공하고 Firebase Realtime Database의 실시간 데이터 동기화를 처리

   
