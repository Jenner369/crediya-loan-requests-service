# Etapa de build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiamos los archivos necesarios para Gradle
COPY . .

# Compilamos todo con Gradle global
RUN ./gradlew clean build -x validateStructure -x test --no-daemon

# Etapa de ejecución
FROM eclipse-temurin:17-jdk-alpine AS runtime
WORKDIR /app

# Copiamos el JAR global compilado
COPY --from=build /app/applications/app-service/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]