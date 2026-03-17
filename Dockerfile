# syntax=docker/dockerfile:1.4

# Build stage: use Gradle image and keep Gradle cache with BuildKit cache mounts
FROM gradle:8.7-jdk17 AS build
WORKDIR /app

# Copy only build files first to leverage Docker layer caching for dependencies
COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./

# Make the Gradle wrapper executable and warm the dependency cache
RUN chmod +x gradlew
RUN --mount=type=cache,target=/home/gradle/.gradle \
	gradle --version || true

# Copy full project and build using cached Gradle directory
COPY . .
RUN --mount=type=cache,target=/home/gradle/.gradle \
	gradle bootJar -x test --no-daemon --info --console=plain

# Package stage: smaller runtime image
FROM amazoncorretto:17-al2-jdk
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create runtime directories
RUN mkdir -p /app/files /app/tmp

# Configure timezone (America/Bogota)
RUN rm -f /etc/localtime && ln -s /usr/share/zoneinfo/America/Bogota /etc/localtime

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]