# Movete WhatsApp QR Backend

Backend Spring Boot para el módulo de WhatsApp QR de Movete.

## Ejecutar local

1. Configura variables de entorno o un archivo `.env`.
2. Ejecuta `./run-local.ps1` o `./gradlew.bat bootRun`.

### Perfil test fuera de JUnit

Si quieres levantar la aplicación con el perfil `test` usando `bootRun`, Spring solo leerá archivos de `src/main/resources`. Por eso existe una copia de `application-test.yml` también en `src/main/resources`.

Ejemplo:

```powershell
$env:SPRING_PROFILES_ACTIVE='test'
$env:SERVER_PORT='8081'
./gradlew.bat bootRun
```

Si usas `8080` y ya hay otro proceso escuchando, el arranque fallará con `Port 8080 was already in use`.

## Probar

- Ejecutar todas las pruebas: `./gradlew.bat test`
- Ejecutar una clase puntual: `./gradlew.bat test --tests "*ProductoCrudControllerTest"`

## Métricas

El proyecto ya expone métricas de Spring Boot y JVM en estos endpoints:

- `GET /actuator/health`
- `GET /actuator/metrics`
- `GET /actuator/prometheus`

## OpenTelemetry para Grafana

Se agregó soporte para exportar métricas por OTLP usando Micrometer cuando se activa el perfil `otel`.

### Qué habilita

- métricas HTTP del servidor
- métricas de JVM
- métricas de proceso
- histogramas para `http.server.requests`
- exportación OTLP a un collector, Grafana Alloy o Grafana Cloud

### Cómo activarlo

Activa el perfil `otel` junto con tu perfil actual.

Ejemplo en PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE='prod,otel'
$env:OTEL_EXPORTER_OTLP_METRICS_ENDPOINT='http://localhost:4318/v1/metrics'
./gradlew.bat bootRun
```

### Variables soportadas

- `OTEL_EXPORTER_OTLP_METRICS_ENDPOINT`: endpoint OTLP HTTP para métricas. Default: `http://localhost:4318/v1/metrics`
- `OTEL_METRICS_ENABLED`: habilita o deshabilita la exportación OTLP. Default: `true` cuando el perfil `otel` está activo
- `OTEL_EXPORTER_OTLP_METRICS_STEP`: frecuencia de exportación. Default: `30s`
- `OTEL_EXPORTER_OTLP_METRICS_BATCH_SIZE`: tamaño de lote. Default: `10000`
- `OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY`: temporalidad OTLP. Default: `cumulative`
- `OTEL_SERVICE_NAME`: nombre del servicio reportado a OTel. Default: `spring.application.name`
- `OTEL_SERVICE_NAMESPACE`: namespace del servicio. Default: `movete`
- `OTEL_ENVIRONMENT`: ambiente reportado. Default: perfil activo
- `OTEL_SERVICE_INSTANCE_ID`: identificador de instancia. Default: `HOSTNAME` o `COMPUTERNAME`

### Headers para Grafana Cloud

Si tu destino OTLP exige autenticación, define el header como variable de entorno siguiendo el formato de Spring Boot.

Ejemplo:

```powershell
$env:SPRING_PROFILES_ACTIVE='prod,otel'
$env:OTEL_EXPORTER_OTLP_METRICS_ENDPOINT='https://otlp-gateway-prod-us-central-0.grafana.net/otlp/v1/metrics'
$env:MANAGEMENT_OTLP_METRICS_EXPORT_HEADERS_AUTHORIZATION='Basic <token-base64>'
./gradlew.bat bootRun
```

### Flujo recomendado

1. Aplicación Spring Boot exporta métricas OTLP.
2. Un collector, Alloy o Grafana Cloud recibe las métricas.
3. Grafana consulta esas métricas desde tu backend de observabilidad.