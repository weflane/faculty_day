FROM eclipse-temurin:17-jdk
WORKDIR /app
RUN apt-get update && apt-get install -y wget unzip && \
    wget -q https://github.com/JetBrains/kotlin/releases/download/v1.9.22/kotlin-compiler-1.9.22.zip && \
    unzip -q kotlin-compiler-1.9.22.zip && \
    rm kotlin-compiler-1.9.22.zip

COPY src/main/kotlin/dz2.kt .
RUN kotlinc/bin/kotlinc dz2.kt -include-runtime -d app.jar

# Запускаем
ENTRYPOINT ["java", "-jar", "app.jar"]