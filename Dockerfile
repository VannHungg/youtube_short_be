# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Run stage
FROM eclipse-temurin:17-jdk-jammy
ARG APP_VERSION=1.0.0
WORKDIR /app

# Install yt-dlp và ffmpeg
RUN apk add --no-cache python3 py3-pip ffmpeg && pip3 install --no-cache-dir yt-dlp

# Tạo thư mục để lưu videos
RUN mkdir -p /app/videos && chmod 777 /app/videos

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV JAR_VERSION=${APP_VERSION}
CMD java -jar youtube-${JAR_VERSION}.jar