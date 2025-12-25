# 1. Собираем приложение
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:resolve

COPY src src
RUN ./mvnw package -DskipTests

# 2. Лёгкий образ для запуска
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# копируем собранный jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
