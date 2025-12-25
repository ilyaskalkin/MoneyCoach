# 1. Сборка приложения
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# копируем pom и исходники
COPY pom.xml .
COPY src src

# собираем jar
RUN mvn clean package -DskipTests

# 2. Образ для запуска
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# копируем собранный jar
COPY --from=build /app/target/moneycoach-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
