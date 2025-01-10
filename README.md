# 따따 Android
따따 프로젝트의 안드로이드 리포지토리입니다.

# Tech Stack

다음은 프로젝트의 구현을 위해 사용하는 기술 스택을 정리한 표입니다.

| 이름 | 설명 |
| - | - |
| Kotlin | 프로그래밍 언어 |
| Jetpack Compose | 인-코드 선언형 앱 설계 |
| Git | 체계적인 코드 관리 및 협업 |

다음과 같은 라이브러리 의존성을 가지고 있습니다.

| 이름 | 버전 | 설명 |
| - | - | - |
| Jetpack Navigation | 2.0.21 | 화면 전환 관리를 위한 라이브러리 |
| Hilt | 2.51.1 | 의존성 주입을 위한 라이브러리 |
| Retrofit2 | 2.11.0 | HTTP 통신을 위한 라이브러리 |
| SharedPreference | 1.2.1 | 로컬 데이터 저장 라이브러리 |
| Room | 2.6.1 | 로컬 데이터베이스 라이브러리 |
| Naver Map SDK | 3.20.0 | 네이버 지도 사용을 위한 SDK |
| KakaoSDK | 2.20.6 | 카카오 로그인을 위한 SDK |
| Android JUnit | 1.2.1 | 단위 테스트를 위한 라이브러리 |

본 프로젝트는 멀티 모듈 아키텍쳐를 기반으로, 뷰모델을 사용한 MVVM 디자인 패턴으로 구성합니다.

# Conventions

다음은 본 프로젝트에 기여하는 개발자가 지켜야 할 컨벤션입니다.

## Branch

본 프로젝트는 Gitflow 브랜치 전략을 따릅니다.

<div align=center>
    <img src="https://techblog.woowahan.com/wp-content/uploads/img/2017-10-30/git-flow_overall_graph.png" width=50% alt="브랜치 전략 설명 이미지"/>
</div>

- `master`: 배포 가능한 단위의 브랜치
- `release`: 배포 전 테스트가 가능한 단위의 브랜치
- `develop`: 개발 중인 브랜치
- `feature/#issue_number`: 개발 단위별 브랜치
- `hotfix`: `master` 브랜치의 긴급 버그 수정 브랜치

모든 기능 개발은 다음 흐름을 따릅니다.

1. 개발하고자 하는 기능에 대한 이슈를 등록하여 번호를 발급합니다.
2. `develop` 브랜치로부터 분기하여 이슈 번호를 사용해 이름을 붙인 `feature` 브랜치를 만든 후 작업합니다.
3. 작업이 완료되면 `develop` 브랜치에 풀 요청을 작성하고, 팀원의 동의를 얻으면 병합합니다.

## Commit

커밋은 [Gitmoji](https://gitmoji.dev/)를 사용해 시각적으로 작성합니다. 다음은 본 프로젝트의 커밋 형식입니다. 각 줄 사이에는 빈 줄이 추가로 있음에 주의해주세요.

```text
[깃모지] [제목]

[본문]

[이슈 번호 참조(선택)]
```

예시)

```text
:bug: 버튼 버그 수정

키보드 콜백이 불러지지 않는 버그를 수정

관련 이슈 번호: #123, #234
```

각 깃모지의 의미는 [이 블로그](https://treasurebear.tistory.com/70)를 참고합니다. [Android Studio 제공 플러그인](https://plugins.jetbrains.com/plugin/12383-gitmoji-plus-commit-button)을 사용하여 깃모지를 편리하게 이용할 수 있습니다.

## Issue

이슈는 본 리포지토리에 등록된 목적에 맞는 이슈 템플릿을 사용하여 작성합니다.

- `Feature Template`: 기능 추가를 위한 이슈에 사용
- `Bug Template`: 버그 수정을 위한 이슈에 사용

## Pull Request

풀 요청은 본 리포지토리에 등록된 템플릿을 사용하여 작성합니다.

## Code

코드의 스타일은 [Android 공식문서의 Kotlin 스타일 가이드](https://developer.android.com/kotlin/style-guide?hl=ko)를 최대한 따릅니다. 다음은 주요 네이밍 규칙입니다.

- 작성되는 모든 소스 파일은 UTF-8로 인코딩되어야 합니다.
- 코틀린 파일의 제목은 되도록이면 `PascalCase`를 사용하여야 합니다.
- 컴포저블 함수의 이름은 `PascalCase`, 그 외 함수의 이름은 동사로 시작하는 `camelCase`를 사용하며, 변수명은 `camelCase`를 사용합니다. (람다식을 저장하는 변수도 `camelCase`를 사용합니다.)
- 콜백 함수를 전달하는 변수일 경우 `on`으로 시작합니다. ex) `onButtonClicked`, `onDataLoaded`
- 안드로이드 스튜디오 상의 IDE의 노란 줄에 주의합니다.

# Development Environment

다음은 본 프로젝트의 안드로이드 개발 환경입니다.

- `targetSDK`: 35, `minSDK`: 26
- 안드로이드 스튜디오 버전: Ladybug | 2024.2.1 또는 그 히상
- 테스트 환경: 안드로이드 스튜디오 제공 에뮬레이터(AVD)
- - 기기명: Pixel 8
  - API 35 (Android 15.0, x86_64)
  - 1080 x 2400 px (412 x 915 dp)
