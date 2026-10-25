# Galaxy Wallpaper — Android Studio 프로젝트

갤럭시 기종 선택, 세로형 4K 배경화면 샘플 3종, 수동 적용, 화면 꺼짐 자동 변경(프로세스가 살아 있는 동안)을 제공하는 초기 프로젝트입니다.

## APK 빌드 방법
1. Android Studio 최신 버전을 설치합니다.
2. `GalaxyWallpaper` 폴더를 Android Studio에서 엽니다.
3. Android SDK Platform 35가 설치되어 있는지 확인합니다.
4. Gradle 동기화를 완료한 뒤 **Build > Build APK(s)** 를 선택합니다.
5. APK는 `app/build/outputs/apk/debug/app-debug.apk` 에 생성됩니다.

## 현재 범위와 제한
- 포함된 배경화면은 데모용으로 생성한 2160 × 3840 JPEG 3장입니다. 실제 상용 앱에는 라이선스가 확보된 배경화면을 추가해야 합니다.
- 기종 선택은 UI/설정용입니다. 실제 화면 해상도와 비율은 OS가 적용 시 처리하며, 기종별 정확한 해상도 데이터베이스는 아직 포함되지 않았습니다.
- `ACTION_SCREEN_OFF` 리시버는 앱 프로세스가 살아 있는 동안에만 감지합니다. 프로세스 종료, 절전/백그라운드 제한 상황에서 화면 꺼짐 즉시 변경은 보장되지 않습니다. 안정적인 자동 변경을 제품 수준으로 구현하려면 Android 버전별 테스트와 백그라운드 실행 정책 검토가 필요합니다.
- Android APK는 갤럭시 등 Android 기기용입니다.
