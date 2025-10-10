FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN /opt/gradle/bin/gradle clean build -x test

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
