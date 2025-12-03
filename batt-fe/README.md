# batt-fe

## 시작하기

### 1. 종속성 설치

```bash
npm install
```

### 2. 개발 서버 실행

```bash
npm run dev
```

## 폴더 구조

```
my-vite-app/
├── public/           # 정적 에셋 (빌드 시 그대로 복사됨)
├── src/              # 소스 코드
│   ├── app/          # 전역 설정 및 스타일
│   ├── assets/       # 컴포넌트에서 사용하는 에셋
│   ├── components/   # 재사용 가능한 UI 컴포넌트
│   ├── features/     # 기능 단위 모듈
│   ├── hooks/        # 커스텀 React Hooks
│   ├── lib/          # 유틸리티 함수 및 라이브러리
│   ├── styles/       # 전역 스타일시트
│   ├── types/        # TypeScript 타입 정의
│   ├── main.tsx      # 애플리케이션 진입점
│   └── App.tsx       # 메인 애플리케이션 컴포넌트
├── package.json      # 프로젝트 정보 및 스크립트
└── README.md         # 프로젝트 문서
```

## 사용 가능한 스크립트

- `npm run dev`: 개발 모드로 Vite 서버를 실행합니다. HMR(Hot Module Replacement)이 활성화되어 있어 코드 변경 시 즉시 반영됩니다.
- `npm run build`: 프로덕션용으로 애플리케이션을 빌드합니다. 결과물은 `dist` 폴더에 생성됩니다.
- `npm run lint`: ESLint를 사용하여 코드 스타일 및 잠재적인 오류를 검사합니다.
- `npm run lint:fix`: ESLint가 자동으로 수정할 수 있는 문제들을 수정합니다.
- `npm run format`: Prettier를 사용하여 프로젝트 전체 코드의 형식을 맞춥니다.
- `npm run preview`: `dist` 폴더에 빌드된 프로덕션 버전을 로컬에서 미리 확인합니다.
