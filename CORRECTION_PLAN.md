# 📋 PLAN DE CORRECCIÓN - NullPointerException

**Proyecto:** MoveteSwitchs WhatsApp QR App Backend  
**Fecha:** 2026-04-10  
**Prioridad:** 🔴 CRÍTICA  
**Tiempo Estimado:** 4-6 horas total

---

## 📅 Cronograma de Fases

### FASE 1️⃣: CRÍTICA (2-3 horas)
Corrige 5 problemas críticos que bloquean autenticación y órdenes

- ✅ Corrección 1: JwtUtil.java (validaciones de claims)
- ✅ Corrección 2: JwtRequestFilter.java (null checks)
- ✅ Corrección 3: OrderDetailsService.java (validar Company)
- ✅ Corrección 4: OrderDetailsDeliveryService.java (validar Customer)
- ✅ Corrección 5: JwtUtilUser.java (isTokenExpired)

**Estado:** Pendiente de ejecución

### FASE 2️⃣: COMPLEMENTARIA (1 hora)
Corrige problemas moderados y mejora robustez

- ⏳ Corrección 6: SearchDTOConverter.java
- ⏳ Corrección 7: Company.java (revisión)
- ⏳ Corrección 8: JwtRequestFilter.java (messageService)

**Estado:** Pendiente

### FASE 3️⃣: VALIDACIÓN (1-2 horas)
Pruebas y verificación de cambios

- ⏸️ Tests unitarios
- ⏸️ Compilación limpia
- ⏸️ Code review
- ⏸️ Pruebas funcionales

**Estado:** Pendiente

---

## 🔴 CORRECCIÓN 1: JwtUtil.java - Validar Claims

**Líneas:** 65, 69, 73  
**Severidad:** 🔴 CRÍTICO  
**Tiempo:** ~15 minutos

### Problema Actual
```java
public Long extractExternalCompanyId(String token) {
    return extractClaim(token, claims -> claims.get("externalCompanyId", Long.class));
    // ❌ Retorna null sin validación
}

public String extractSessionId(String token) {
    return extractClaim(token, claims -> claims.get("sessionId", String.class));
    // ❌ Retorna null sin validación
}

public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", Long.class));
    // ❌ Retorna null sin validación
}
```

### Solución Propuesta
```java
public Long extractExternalCompanyId(String token) {
    Long companyId = extractClaim(token, claims -> claims.get("externalCompanyId", Long.class));
    if (companyId == null) {
        log.warn("Token missing required claim: externalCompanyId");
        throw new JwtException("Token must contain externalCompanyId claim");
    }
    return companyId;
}

public String extractSessionId(String token) {
    String sessionId = extractClaim(token, claims -> claims.get("sessionId", String.class));
    if (sessionId == null || sessionId.isEmpty()) {
        log.warn("Token missing or empty sessionId claim");
        throw new JwtException("Token must contain valid sessionId claim");
    }
    return sessionId;
}

public Long extractUserId(String token) {
    Long userId = extractClaim(token, claims -> claims.get("userId", Long.class));
    if (userId == null) {
        log.warn("Token missing required claim: userId");
        throw new JwtException("Token must contain userId claim");
    }
    return userId;
}
```

### Cambios Requeridos
1. Agregar validaciones null después de extraer claims
2. Lanzar JwtException si claim requerido está ausente
3. Agregar logs de advertencia

### Validación Post-Corrección
```bash
# Compilar solo este archivo
./gradlew compileJava

# Test: Debe rechazar tokens sin claims requeridos
curl -X GET http://localhost:8080/api/test \
  -H "Authorization: Bearer <token_sin_externalCompanyId>"
# Resultado esperado: 401 Unauthorized
```

---

## 🔴 CORRECCIÓN 2: JwtRequestFilter.java - Null Checks

**Líneas:** 48, 54, 55, 63, 66  
**Severidad:** 🔴 CRÍTICO  
**Tiempo:** ~20 minutos

### Problema Actual
```java
Long externalCompanyId = jwtUtil.extractExternalCompanyId(token);
String sessionId = jwtUtil.extractSessionId(token);
// ❌ Directamente después de extraer, sin validar null

if (sessionId == null || !sessionRegistryService.isSessionActive(sessionId)) {
    // ❌ messageService puede ser null
    response.sendError(..., messageService.getMessage("session.invalid"));
}

List<String> authorities = (List<String>) claims.get("authorities");
// ❌ Cast sin validación de tipo
```

### Solución Propuesta
```java
try {
    Claims claims = jwtUtil.extractAllClaims(token);
    log.debug("Claims extracted: {}", claims);

    if (jwtUtil.isTokenValid(token)) {
        Long externalCompanyId = jwtUtil.extractExternalCompanyId(token);
        String sessionId = jwtUtil.extractSessionId(token);
        
        // ✅ Validaciones adicionales
        if (externalCompanyId == null) {
            log.warn("Token has null externalCompanyId");
            sendUnauthorizedError(response, "Invalid token: missing company");
            return;
        }
        
        if (sessionId == null || !sessionRegistryService.isSessionActive(sessionId)) {
            log.warn("Invalid or inactive session for token, sessionId={}", sessionId);
            String errorMsg = "Session invalid or expired";
            if (messageService != null) {
                errorMsg = messageService.getMessage("session.invalid");
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMsg);
            return;
        }

        // ✅ Cast seguro
        List<String> authorities = null;
        Object authObj = claims.get("authorities");
        if (authObj instanceof List<?>) {
            try {
                @SuppressWarnings("unchecked")
                List<String> parsedAuth = (List<String>) authObj;
                authorities = parsedAuth;
            } catch (ClassCastException e) {
                log.warn("Invalid authorities format in token", e);
            }
        }

        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(
                externalCompanyId,
                null,
                authorities != null && !authorities.isEmpty()
                    ? authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
                    : null
            );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug("Authentication set in SecurityContext for company: {}", externalCompanyId);
    }
} catch (ExpiredJwtException e) {
    // ... manejo existente de excepciones catch
}

// ✅ Método helper
private void sendUnauthorizedError(HttpServletResponse response, String message) 
        throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.getWriter().write("{\"error\": \"" + message + "\"}");
}
```

### Cambios Requeridos
1. Validar externalCompanyId no sea null antes de usar
2. Validar sessionId no sea null antes de usar
3. Proteger messageService.getMessage() con null check
4. Cast seguro de authorities con instanceof

### Validación Post-Corrección
```bash
# Test con token válido
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer <token_válido>"
# Resultado esperado: 200 OK

# Test con token inválido
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer invalid_token"
# Resultado esperado: 401 Unauthorized
```

---

## 🔴 CORRECCIÓN 3: OrderDetailsService.java - Validar Company

**Línea:** 668  
**Severidad:** 🔴 CRÍTICO  
**Tiempo:** ~10 minutos
**Métodos afectados:** confirmedOreders(), noConfirmationOrder()

### Problema Actual
```java
Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
log.info("confirmedOreders - start, tableNumber={}, phone={}, companyId={}", 
    tableNumber, phoneNumber, company.getId());  // ❌ NPE si company es null
```

### Solución Propuesta
```java
Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
if (company == null) {
    log.warn("confirmedOreders - Company not found for externalCompanyId={}", tokenCompanyId);
    throw new DomainException(
        DomainErrorCode.INVALID_REQUEST,
        "Compañía no encontrada en la base de datos"
    );
}
log.info("confirmedOreders - start, tableNumber={}, phone={}, companyId={}", 
    tableNumber, phoneNumber, company.getId());
```

### Búsqueda de Todos los Símiles
También necesitas revisar y corregir:
- `noConfirmationOrder()` - Línea ~630 (similar)
- Cualquier otro método que acceda a company.getId()

### Cambios Requeridos
1. Agregar null check después de findByExternalCompanyId()
2. Lanzar DomainException si company no existe
3. Aplicar el mismo patrón a otros métodos

### Validación Post-Corrección
```bash
# Test con compañía válida
curl -X GET "http://localhost:8080/api/orders/confirmed?tableNumber=1&phoneNumber=1234567890" \
  -H "Authorization: Bearer <valid_token>"
# Resultado esperado: 200 OK con órdenes

# Test con token de compañía inexistente
# (Requiere token con externalCompanyId de compañía no existente)
# Resultado esperado: 400 Bad Request
```

---

## 🔴 CORRECCIÓN 4: OrderDetailsDeliveryService.java - Validar Customer

**Línea:** 305  
**Severidad:** 🔴 CRÍTICO  
**Tiempo:** ~15 minutos
**Método:** updateOrder()

### Problema Actual
```java
Customer customer = customerRepository.findByPhone(orderDetailsDeliveryDTO.getPhone());

// ❌ Sin validación de null - múltiples NPE potenciales
customer.setTypeIdentificationId(orderDetailsDeliveryDTO.getTypeIdentificationId());
customer.setNumerIdentification(orderDetailsDeliveryDTO.getNameIdentification());
customer.setName(orderDetailsDeliveryDTO.getNameClient());
customer.setAddress(orderDetailsDeliveryDTO.getAddress());
customer.setPhone(orderDetailsDeliveryDTO.getPhone());
customer.setEmail(orderDetailsDeliveryDTO.getMail());

customerRepository.save(customer);
```

### Solución Propuesta
```java
Customer customer = customerRepository.findByPhone(orderDetailsDeliveryDTO.getPhone());
if (customer == null) {
    log.warn("updateOrder - Customer not found for phone: {}", 
        orderDetailsDeliveryDTO.getPhone());
    throw new DomainException(
        DomainErrorCode.INVALID_REQUEST,
        "Cliente no encontrado para el teléfono: " + orderDetailsDeliveryDTO.getPhone()
    );
}

// ✅ Ahora es seguro
customer.setTypeIdentificationId(orderDetailsDeliveryDTO.getTypeIdentificationId());
customer.setNumerIdentification(orderDetailsDeliveryDTO.getNameIdentification());
customer.setName(orderDetailsDeliveryDTO.getNameClient());
customer.setAddress(orderDetailsDeliveryDTO.getAddress());
customer.setPhone(orderDetailsDeliveryDTO.getPhone());
customer.setEmail(orderDetailsDeliveryDTO.getMail());

customerRepository.save(customer);
```

### Cambios Requeridos
1. Agregar null check después de findByPhone()
2. Lanzar DomainException si customer no existe
3. Considerar crear cliente si no existe (alternativa)

### Validación Post-Corrección
```bash
# Test con cliente válido
curl -X POST http://localhost:8080/api/orders/delivery/update \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <valid_token>" \
  -d '{"phone":"1234567890","...": "..."}'
# Resultado esperado: 200 OK

# Test con cliente no existente
curl -X POST http://localhost:8080/api/orders/delivery/update \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <valid_token>" \
  -d '{"phone":"9999999999","...": "..."}'
# Resultado esperado: 400 Bad Request
```

---

## 🔴 CORRECCIÓN 5: JwtUtilUser.java - isTokenExpired()

**Líneas:** 54, 59  
**Severidad:** 🔴 CRÍTICO  
**Tiempo:** ~15 minutos

### Problema Actual
```java
public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
    // ❌ NPE si extractExpiration retorna null
}

public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    // ❌ NPE si username es null
}
```

### Solución Propuesta
```java
public boolean isTokenExpired(String token) {
    try {
        Date expiration = extractExpiration(token);
        if (expiration == null) {
            log.warn("Token has no expiration date");
            return true;  // Considerar como expirado por defecto
        }
        return expiration.before(new Date());
    } catch (Exception e) {
        log.warn("Error validating token expiration: {}", e.getMessage());
        return true;  // Token expirado por defecto en caso de error
    }
}

public boolean validateToken(String token, UserDetails userDetails) {
    try {
        final String username = extractUsername(token);
        if (username == null) {
            log.warn("Token has no username");
            return false;
        }
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    } catch (Exception e) {
        log.warn("Token validation failed: {}", e.getMessage());
        return false;
    }
}
```

### Cambios Requeridos
1. Validar que extractExpiration() no sea null
2. Validar que extractUsername() no sea null
3. Agregar try-catch para manejo de excepciones
4. Retornar false/true seguros por defecto

### Validación Post-Corrección
```bash
# Compile y verifica
./gradlew compileJava

# Test: Token válido debe pasar validación
./gradlew test -Dtest=JwtUtilUserTest
```

---

## 🟠 CORRECCIÓN 6: SearchDTOConverter.java - Mejoras Null

**Líneas:** 145, 156  
**Severidad:** 🟠 MODERADO  
**Tiempo:** ~10 minutos

### Problema Actual
```java
private static int getInt(Map<String, String> map, String key, int defaultValue) {
    if (map == null || !map.containsKey(key)) {
        return defaultValue;
    }
    try {
        return Integer.parseInt(map.get(key));  // ⚠️ map.get(key) puede ser null
    } catch (NumberFormatException e) {
        return defaultValue;
    }
}
```

### Solución Propuesta
```java
private static int getInt(Map<String, String> map, String key, int defaultValue) {
    if (map == null || !map.containsKey(key)) {
        return defaultValue;
    }
    String value = map.get(key);
    if (value == null || value.isEmpty()) {
        return defaultValue;
    }
    try {
        return Integer.parseInt(value);
    } catch (NumberFormatException e) {
        log.warn("Invalid integer value for key {}: {}", key, value);
        return defaultValue;
    }
}

private static Long getLong(Map<String, String> map, String key, Long defaultValue) {
    if (map == null || !map.containsKey(key)) {
        return defaultValue;
    }
    String value = map.get(key);
    if (value == null || value.isEmpty()) {
        return defaultValue;
    }
    try {
        return Long.parseLong(value);
    } catch (NumberFormatException e) {
        log.warn("Invalid long value for key {}: {}", key, value);
        return defaultValue;
    }
}
```

### Cambios Requeridos
1. Validar que map.get(key) no sea null
2. Validar que el valor no esté vacío
3. Agregar logs de advertencia

---

## 🟠 CORRECCIÓN 7: JwtRequestFilter.java - messageService Null Safe

**Línea:** 63  
**Severidad:** 🟠 MODERADO  
**Tiempo:** ~5 minutos

### Problema Actual
```java
@Autowired(required = false)
private ErrorMessageService messageService;

// En doFilterInternal:
response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
    messageService.getMessage("session.invalid"));  // ❌ NPE si messageService es null
```

### Solución Propuesta
```java
private String getErrorMessage(String key, String defaultMessage) {
    if (messageService == null) {
        log.debug("messageService not available, using default message");
        return defaultMessage;
    }
    try {
        return messageService.getMessage(key);
    } catch (Exception e) {
        log.warn("Error getting message for key {}: {}", key, e.getMessage());
        return defaultMessage;
    }
}

// En doFilterInternal:
response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
String errorMsg = getErrorMessage("session.invalid", "Session invalid or expired");
response.sendError(HttpServletResponse.SC_UNAUTHORIZED, errorMsg);
```

---

## ✅ VALIDACIÓN FINAL

### Paso 1: Compilación Limpia
```bash
# Compilar todo el proyecto
./gradlew clean compileJava -x test

# Resultado esperado:
# BUILD SUCCESSFUL in X ms
```

### Paso 2: Tests Unitarios
```bash
# Ejecutar tests de autenticación
./gradlew test --tests "*Jwt*"
./gradlew test --tests "*OrderDetails*"

# Resultado esperado:
# Todos los tests pasan
```

### Paso 3: Build JAR
```bash
# Construir JAR
./gradlew build -x test

# Resultado esperado:
# BUILD SUCCESSFUL
```

### Paso 4: Code Review Checklist
- [ ] Las líneas de código coinciden con el plan
- [ ] No hay nuevos warnings del compilador
- [ ] Tests existentes siguen pasando
- [ ] Nuevos null checks tienen logs apropiados
- [ ] Excepciones se lanzan con mensajes descriptivos
- [ ] No hay cambios innecesarios

---

## 📊 Orden de Ejecución Recomendado

### Sesión 1 (60-90 minutos)
1. **Corrección 1:** JwtUtil.java (15 min)
   - Compilar
   - Test rápido

2. **Corrección 2:** JwtRequestFilter.java (20 min)
   - Compilar
   - Test rápido

3. **Corrección 3:** OrderDetailsService.java (10 min)
   - Compilar
   - Búsqueda de símiles

4. **Corrección 4:** OrderDetailsDeliveryService.java (15 min)
   - Compilar
   - Test rápido

5. **Corrección 5:** JwtUtilUser.java (15 min)
   - Compilar
   - Test unitario

### Sesión 2 (30-45 minutos)
6. **Corrección 6:** SearchDTOConverter.java (10 min)
7. **Corrección 7:** messageService null safe (5 min)
8. **Validación Final:** Compilación, tests, code review (20-30 min)

---

## 🚀 Comando Rápido Terminal

```bash
# Compilar limpio después de cambios
cd "c:\Users\LENOVO\OneDrive - Arquitecsoft S.A.S\Arquitecsoft\Unidad_E\Git\Swithcs\ProyectoMoveteArqui\github_dokploy\MoveteSwitchs_arqmv-module-back-whatsapp-qr-app-backend"

# Limpiar y compilar
./gradlew.bat clean compileJava -x test

# Si compilación es correcta:
./gradlew.bat build -x test
```

---

## 📝 Métricas de Éxito

| Métrica | Objetivo | Estado |
|---------|----------|--------|
| Compilación limpia | 100% | ⏳ Pendiente |
| Tests pasando | 100% | ⏳ Pendiente |
| NPE en runtime | 0 | ⏳ Pendiente |
| Code coverage | >80% | ⏳ Pendiente |
| Tiempo total | <6 horas | ⏳ En progreso |

---

## 📞 Próximos Pasos

1. ✅ Leer este plan
2. 🚀 Comenzar con Corrección 1 (JwtUtil.java)
3. 🔄 Seguir orden secuencial
4. ✔️ Validar después de cada corrección
5. 📋 Marcar completado en este documento

---

**Documento:** Plan de Corrección Detallado  
**Fecha:** 2026-04-10  
**Status:** 🟡 LISTO PARA EJECUTAR
