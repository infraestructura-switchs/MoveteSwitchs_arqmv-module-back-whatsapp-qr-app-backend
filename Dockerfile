# Build stage
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Package stage
FROM amazoncorretto:17-al2-jdk
WORKDIR /apl/

# Copiar el jar desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Crear directorios necesarios
RUN mkdir -p /apl/files/
RUN mkdir -p /apl/tmp/

# Configurar Timezone (America/Bogota) en Amazon Linux 2
RUN rm -f /etc/localtime && ln -s /usr/share/zoneinfo/America/Bogota /etc/localtime

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]