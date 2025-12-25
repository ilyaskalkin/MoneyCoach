# 1. Сборка jar внутри контейнера
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:resolve

COPY src src
RUN ./mvnw clean package -DskipTests

# 2. Образ для запуска
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# копируем jar
COPY --from=build /app/target/moneycoach-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
