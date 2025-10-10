FROM gradle:8.5-jdk17 AS build

WORKDIR /app

COPY . .

FROM openjdk:17

RUN gradle clean build -x test

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
