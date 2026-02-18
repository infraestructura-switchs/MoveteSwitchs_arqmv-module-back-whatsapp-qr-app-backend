# Build stage
FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build -x test --no-daemon

# Package stage
FROM amazoncorretto:17-al2-jdk
WORKDIR /apl/

# Copiar el jar desde la etapa de compilación (Gradle lo pone en build/libs)
COPY --from=build /app/build/libs/*.jar app.jar

# Crear directorios necesarios
RUN mkdir -p /apl/files/
RUN mkdir -p /apl/tmp/

# Configurar Timezone (America/Bogota) en Amazon Linux 2
RUN rm -f /etc/localtime && ln -s /usr/share/zoneinfo/America/Bogota /etc/localtime

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]