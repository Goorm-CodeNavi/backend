FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Gradle 관련 파일들 먼저 복사
COPY build.gradle ./
COPY settings.gradle ./
COPY gradle/ gradle/
COPY gradlew ./
COPY gradlew.bat ./

# 소스 코드 복사
COPY src/ src/

# 권한 설정 후 빌드
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 실행 스테이지
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]