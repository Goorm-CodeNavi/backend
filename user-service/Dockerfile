FROM gradle:8.5-jdk17 AS build

WORKDIR /app

COPY . .

FROM openjdk:17

#RUN chmod +x ./gradlew
#RUN ./gradlew clean build -x test

# 파일 위치 확인 (디버깅)
RUN echo "=== 현재 디렉토리 ===" && pwd
RUN echo "=== 전체 파일 목록 ===" && find . -name "*.jar" -type f
RUN echo "=== build 폴더 내용 ===" && ls -la build/ || echo "build 폴더 없음"
RUN echo "=== libs 폴더 내용 ===" && ls -la build/libs/ || echo "libs 폴더 없음"

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
