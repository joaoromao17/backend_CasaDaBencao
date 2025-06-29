# Etapa 1: build com Maven e Java 21
FROM maven:3.9-eclipse-temurin AS builder
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

# Etapa 2: imagem leve com Java apenas
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
