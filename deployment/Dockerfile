# Etapa de build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiamos todo el proyecto (para compilar si se necesita)
COPY . .

# Compilamos con Gradle (se puede desactivar si ya tienes JAR)
RUN ./gradlew clean build -x validateStructure -x test --no-daemon

# Etapa de ejecución
FROM eclipse-temurin:17-jdk-alpine AS runtime
WORKDIR /app

# Creamos un usuario no root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Si existe el jar preconstruido lo copiamos, si no usamos el compilado
ARG JAR_FILE=app.jar
COPY --from=build /app/applications/app-service/build/libs/*.jar /app/app.jar

# Variables de entorno para la JVM
ENV JAVA_OPTS=" \
 -XX:+UseContainerSupport \
 -XX:MaxRAMPercentage=70 \
 -Djava.security.egd=file:/dev/./urandom"

USER appuser

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]