GitHub 저장소를 확인해보니 실제 프로젝트 구조를 반영해서 README를 다시 작성해드릴게요!

# 🚀 CodeNavi - AI 기반 코딩 테스트 해설 플랫폼

<div align="center">
  
  [![Spring](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/)
  [![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
  [![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
  [![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)](https://github.com/features/actions)
  [![AWS](https://img.shields.io/badge/AWS_EC2-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)](https://aws.amazon.com/)
  
</div>

## 📝 프로젝트 소개

**CodeNavi**는 코딩 테스트 문제를 AI가 분석하고 자동으로 해설을 생성해주는 플랫폼입니다. 
사용자가 업로드한 문제를 분석하여 Notion에 자동으로 정리해주는 기능을 제공합니다.

### ✨ 주요 기능

- 🔐 **사용자 관리**: 회원가입, 로그인, 프로필 관리
- 📤 **문제 업로드**: 코딩 테스트 문제 업로드 및 관리
- 🤖 **AI 코드 분석**: Python 기반 자동 코드 분석 엔진 (Groq API 활용)
- 📑 **Notion 자동 업로드**: 분석 결과를 Notion에 자동으로 정리
- 🚀 **CI/CD 자동화**: GitHub Actions를 통한 EC2 자동 배포

## 🛠️ 기술 스택

### Backend
- **Spring Boot 3.3.5** - RESTful API 서버
- **Spring Security** - 인증 및 권한 관리
- **JPA/Hibernate** - ORM
- **MySQL** - 데이터베이스
- **Gradle** - 빌드 도구

### AI Analysis Engine
- **Python 3.x** - 코드 분석 엔진
- **Groq API** - LLM 기반 코드 분석
- **Notion API** - 자동 문서화
- **FastAPI** - Python API 서버

### DevOps
- **Docker & Docker Compose** - 컨테이너화
- **GitHub Actions** - CI/CD 파이프라인
- **AWS EC2** - 클라우드 호스팅
- **Nginx** - 리버스 프록시

## 🎨 디자인 테마

```css
/* Hacker Terminal Theme */
--hacker-green: #00ff41;
--hacker-green-dark: #00cc33;
--hacker-cyan: #00ffff;
--hacker-red: #ff0040;
--hacker-black: #0a0a0a;
--hacker-dark-gray: #1a1a1a;
--hacker-light-gray: #333333;
--hacker-white: #ffffff;
--terminal-bg: #0c0c0c;
--matrix-glow: #00ff41;
```

## 🏗️ 시스템 아키텍처

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │     │                 │
│   Spring Boot   │────▶│  Python Engine  │────▶│   Notion API    │
│   (User/Upload) │     │  (Code Analysis)│     │  (Auto Upload)  │
│                 │     │   + Groq API    │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
         │                       │                        │
         └───────────────────────┴────────────────────────┘
                              │
                     ┌────────────────┐
                     │ Docker Compose │
                     └────────┬───────┘
                              │
                     ┌────────────────┐
                     │ GitHub Actions │
                     └────────┬───────┘
                              │
                     ┌────────────────┐
                     │    AWS EC2     │
                     └────────────────┘
```

## 🚀 시작하기

### 환경 요구사항
- Java 17+
- Python 3.8+
- Docker & Docker Compose
- MySQL 8.0+

### 로컬 실행

1. **저장소 클론**
```bash
git clone https://github.com/Goorm-CodeNavi/backend.git
cd backend
```

2. **환경 변수 설정**
```bash
cp .env.example .env
# .env 파일에 필요한 설정값 입력
```

3. **Docker Compose로 실행**
```bash
docker-compose up -d
```

4. **서비스 접속**
- Spring API: http://localhost:8080
- Python API: http://localhost:8000

## 🔧 환경 설정

### .env 파일 예시

```env
# Docker Hub 설정
DOCKER_USERNAME=your_docker_username

# MySQL 데이터베이스 설정
MYSQL_ROOT_PASSWORD=your_mysql_root_password
MYSQL_DATABASE=codenavi
MYSQL_USER=your_mysql_user
MYSQL_PASSWORD=your_mysql_password

# AI API 설정
GROQ_API_KEY=your_groq_api_key

# Notion API 설정
NOTION_TOKEN=your_notion_integration_token
PARENT_PAGE_ID=your_notion_parent_page_id
```

### GitHub Secrets 설정

GitHub Actions에서 사용하는 Secrets 설정:

| Secret Name | Description |
|------------|-------------|
| `HOST` | EC2 인스턴스 IP 주소 |
| `PRIVATE_KEY` | EC2 접속용 SSH Private Key |
| `DOCKER_USERNAME` | Docker Hub 사용자명 |
| `MYSQL_ROOT_PASSWORD` | MySQL root 비밀번호 |
| `MYSQL_DATABASE` | MySQL 데이터베이스명 |
| `MYSQL_USER` | MySQL 사용자명 |
| `MYSQL_PASSWORD` | MySQL 사용자 비밀번호 |
| `GROQ_API_KEY` | Groq API 키 |
| `NOTION_TOKEN` | Notion Integration 토큰 |
| `PARENT_PAGE_ID` | Notion 상위 페이지 ID |

## 📦 프로젝트 구조

```
backend/
├── .github/
│   └── workflows/
│       └── main.yml         # GitHub Actions CI/CD
├── user-service/                  # Spring Boot 애플리케이션
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       └── resources/
│   ├── Dockerfile
│   ├── build.gradle
│   └── settings.gradle
├── ai-service
│   ├── main.py
│   ├── requirements.txt
│   └── Dockerfile
├── docker-compose.yml       # Docker Compose 설정
├── .env.example            # 환경변수 예시 파일
├── .gitignore
└── README.md
```

## 🚀 배포

GitHub Actions를 통한 자동 배포가 구성되어 있습니다.
`main` 브랜치에 push하면 자동으로 EC2에 배포됩니다.

### 배포 프로세스
1. GitHub Actions가 트리거됨
2. Docker 이미지 빌드 및 Docker Hub에 푸시
3. EC2 인스턴스에 SSH 접속
4. 환경 변수(.env) 파일 동적 생성
5. Docker Compose를 통해 애플리케이션 재시작

## 📝 API 문서

### User API
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인
- `GET /api/users/profile` - 프로필 조회

### Problem API
- `POST /api/problems/upload` - 문제 업로드
- `GET /api/problems/{id}` - 문제 조회
- `POST /api/problems/{id}/analyze` - 코드 분석 요청

### Analysis API (Python)
- `POST /solve-problem` - 코드 분석 실행
- `POST /notion/save` - Notion 페이지 생성
- `POST /notion/quick-save` - Notion 페이지 생성 (인공지능 해설 포함 실행)

## 🌲 브랜치 전략

- `main`: 프로덕션 배포 브랜치
- `develop`: 개발 브랜치
- `feature/*`: 기능 개발 브랜치
- `hotfix/*`: 긴급 수정 브랜치

## 🤝 기여하기

프로젝트에 기여하고 싶으시다면 PR을 보내주세요!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'feat: ✨ Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이센스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📧 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해주세요.

---

<div align="center">
  
  **Made with 💚 by CodeNavi Team**
  
  <img src="https://img.shields.io/badge/Terminal-4EAA25?style=for-the-badge&logo=gnu-bash&logoColor=white" />
  
</div>
