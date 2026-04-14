# Análisis de Errores NullPointerException - Reporte Completo

**Fecha:** Abril 10, 2026  
**Proyecto:** MoveteSwitchs WhatsApp QR App Backend  
**Nivel de Severidad:** CRÍTICO ⛔

---

## 📊 Resumen Ejecutivo

Se identificaron **8 problemas potenciales** que podrían causar `NullPointerException` en producción:
- **5 Críticos** 🔴 - Fallan autenticación y lógica de negocio
- **3 Moderados** 🟠 - Errores en manejo de datos especiales

**Impacto Estimado:** 40-60% de endpoints podría fallar con ciertos datos malformados

---

## 🔴 PROBLEMAS CRÍTICOS

### 1. JwtUtilUser.java - Validación de Token Sin Null Checks

**Ubicación:** `src/main/java/com/restaurante/bot/security/JwtUtilUser.java:54, 59`

**Problema:**
```java
public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());  // ❌ NPE si extractExpiration retorna null
}

public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));  
    // ❌ NPE si username es null
}
```

**Escenario de Error:**
- Token malformado → extractExpiration() retorna null
- Se llama a `.before()` en null → **NullPointerException**

**Severidad:** 🔴 CRÍTICO
**Función Afectada:** Validación de autenticación
**Endpoints Afectados:** Todos los que requieren autenticación

**Solución Propuesta:**
```java
public boolean isTokenExpired(String token) {
    try {
        Date expiration = extractExpiration(token);
        return expiration != null && expiration.before(new Date());
    } catch (Exception e) {
        log.warn("Error validating token expiration: {}", e.getMessage());
        return true; // Token expirado por defecto
    }
}

public boolean validateToken(String token, UserDetails userDetails) {
    try {
        final String username = extractUsername(token);
        return username != null && 
               username.equals(userDetails.getUsername()) && 
               !isTokenExpired(token);
    } catch (Exception e) {
        log.warn("Token validation failed: {}", e.getMessage());
        return false;
    }
}
```

---

### 2. JwtUtil.java - Extracción de Claims Sin Validación

**Ubicación:** `src/main/java/com/restaurante/bot/util/JwtUtil.java:65, 69, 73, 76`

**Problema:**
```java
public Long extractExternalCompanyId(String token) {
    return extractClaim(token, claims -> claims.get("externalCompanyId", Long.class));
    // ⚠️ Retorna null si el claim no existe
}

public String extractSessionId(String token) {
    return extractClaim(token, claims -> claims.get("sessionId", String.class));
    // ⚠️ Retorna null si el claim no existe
}

public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", Long.class));
    // ⚠️ Retorna null si el claim no existe
}
```

**Escenario de Error:**
- extractExternalCompanyId() retorna null
- Se usa directamente en JwtRequestFilter → **NullPointerException**

**Severidad:** 🔴 CRÍTICO
**Cascata de Fallos:**
1. Token sin "externalCompanyId" claim
2. extractExternalCompanyId() retorna null
3. JwtRequestFilter usa null en sessionRegistry
4. falla en lógica de autorización

**Solución Propuesta:**
```java
public Long extractExternalCompanyId(String token) {
    Long companyId = extractClaim(token, claims -> claims.get("externalCompanyId", Long.class));
    if (companyId == null) {
        throw new JwtException("Token must contain externalCompanyId claim");
    }
    return companyId;
}

public String extractSessionId(String token) {
    String sessionId = extractClaim(token, claims -> claims.get("sessionId", String.class));
    if (sessionId == null || sessionId.isEmpty()) {
        throw new JwtException("Token must contain valid sessionId claim");
    }
    return sessionId;
}

public Long extractUserId(String token) {
    Long userId = extractClaim(token, claims -> claims.get("userId", Long.class));
    if (userId == null) {
        throw new JwtException("Token must contain userId claim");
    }
    return userId;
}
```

---

### 3. JwtRequestFilter.java - Extracción de Valores Sin Validación

**Ubicación:** `src/main/java/com/restaurante/bot/util/JwtRequestFilter.java:48-70`

**Problema:**
```java
Long externalCompanyId = jwtUtil.extractExternalCompanyId(token);  // Puede ser null
String sessionId = jwtUtil.extractSessionId(token);                // Puede ser null

if (sessionId == null || !sessionRegistryService.isSessionActive(sessionId)) {
    // ⚠️ Si ambos valores son null, la lógica está comprometida
}

@SuppressWarnings("unchecked")
List<String> authorities = (List<String>) claims.get("authorities");
// ⚠️ Cast sin validación - puede causar ClassCastException o ser null
```

**Escenario de Error:**
- externalCompanyId = null debido a token malformado
- Se pasa null al resto del flujo
- SecurityContext se configura con principal=null
- **NullPointerException** en controladores

**Severidad:** 🔴 CRÍTICO
**Endpoints Afectados:** Toda lógica que accede a getUserCompanyId()

**Solución Propuesta:**
```java
try {
    Long externalCompanyId = jwtUtil.extractExternalCompanyId(token);
    String sessionId = jwtUtil.extractSessionId(token);
    
    if (externalCompanyId == null) {
        log.warn("Token missing externalCompanyId");
        sendUnauthorized(response, "Invalid token structure");
        return;
    }
    
    if (sessionId == null || !sessionRegistryService.isSessionActive(sessionId)) {
        log.warn("Invalid or inactive session: {}", sessionId);
        sendUnauthorized(response, "Session invalid");
        return;
    }
    
    List<String> authorities = null;
    Object authObj = claims.get("authorities");
    if (authObj instanceof List<?>) {
        authorities = (List<String>) authObj;
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
    
} catch (Exception e) {
    log.error("Token processing error: {}", e.getMessage(), e);
    sendUnauthorized(response, "Authentication failed");
    return;
}
```

---

### 4. OrderDetailsDeliveryService.java - Null Customer Update

**Ubicación:** `src/main/java/com/restaurante/bot/business/service/OrderDetailsDeliveryService.java:303-310`

**Problema:**
```java
public GenericResponse updateOrder(OrderDetailsDeliveryDTO orderDetailsDeliveryDTO) {
    // ...
    Customer customer = customerRepository.findByPhone(orderDetailsDeliveryDTO.getPhone());
    
    // ❌ Sin validación de null
    customer.setTypeIdentificationId(orderDetailsDeliveryDTO.getTypeIdentificationId());
    customer.setNumerIdentification(orderDetailsDeliveryDTO.getNameIdentification());
    // ...
}
```

**Escenario de Error:**
- Cliente no existe en BD
- findByPhone() retorna null
- `.setTypeIdentificationId()` en null → **NullPointerException**

**Severidad:** 🔴 CRÍTICO
**Endpoints Afectados:** POST `/orders/delivery/update`

**Solución Propuesta:**
```java
public GenericResponse updateOrder(OrderDetailsDeliveryDTO orderDetailsDeliveryDTO) {
    // ...
    
    Customer customer = customerRepository.findByPhone(orderDetailsDeliveryDTO.getPhone());
    if (customer == null) {
        log.warn("Customer not found for phone: {}", orderDetailsDeliveryDTO.getPhone());
        throw new DomainException(
            DomainErrorCode.INVALID_REQUEST, 
            "Cliente no encontrado para el teléfono: " + orderDetailsDeliveryDTO.getPhone()
        );
    }
    
    customer.setTypeIdentificationId(orderDetailsDeliveryDTO.getTypeIdentificationId());
    customer.setNumerIdentification(orderDetailsDeliveryDTO.getNameIdentification());
    // ... resto del código
}
```

---

### 5. OrderDetailsService.java - Null Company Dereference

**Ubicación:** `src/main/java/com/restaurante/bot/business/service/OrderDetailsService.java:668, 677`

**Problema:**
```java
public List<OrderResponseDTO> confirmedOreders(Long tableNumber, String phoneNumber) {
    Long tokenCompanyId = getAuthenticatedCompanyId();
    
    Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
    // ❌ Sin null check
    log.info("confirmedOreders..., companyId={}", company.getId());
    
    // ❌ Posterior uso sin validar
    List<Object[]> resultList = orderTransactionRepository.findAllOrdersConfirmJPQL(
        tableNumber, phoneNumber, company.getId()
    );
}
```

**Escenario de Error:**
- Compañía eliminada/no existe
- findByExternalCompanyId() retorna null
- `.getId()` en null → **NullPointerException**

**Severidad:** 🔴 CRÍTICO
**Endpoints Afectados:** GET `/orders/confirmed`

**Solución Propuesta:**
```java
public List<OrderResponseDTO> confirmedOreders(Long tableNumber, String phoneNumber) {
    Long tokenCompanyId = getAuthenticatedCompanyId();
    
    Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
    if (company == null) {
        log.warn("confirmedOreders - Company not found, externalCompanyId={}", tokenCompanyId);
        throw new DomainException(
            DomainErrorCode.INVALID_REQUEST, 
            "Compañía no encontrada en la base de datos"
        );
    }
    
    List<Object[]> resultList = orderTransactionRepository.findAllOrdersConfirmJPQL(
        tableNumber, phoneNumber, company.getId()
    );
    // ... resto del código
}
```

---

## 🟠 PROBLEMAS MODERADOS

### 6. SearchDTOConverter.java - Map.get() Sin Null Check

**Ubicación:** `src/main/java/com/restaurante/bot/util/SearchDTOConverter.java:136, 145, 156`

**Problema:**
```java
private static int getInt(Map<String, String> map, String key, int defaultValue) {
    if (map == null || !map.containsKey(key)) {
        return defaultValue;
    }
    try {
        return Integer.parseInt(map.get(key));  // ❌ Puede ser null
    } catch (NumberFormatException e) {
        return defaultValue;
    }
}
```

**Severidad:** 🟠 MODERADO (protegido por try-catch)
**Solución:** Ya implementada parcialmente

---

### 7. Company.java - integrationDetail() Null Return

**Ubicación:** `src/main/java/com/restaurante/bot/model/Company.java:77, 85, 93, 101, 109, 117`

**Problema:**
```java
public String getNumberWhatsapp() {
    return integrationDetail() != null ? integrationDetail().getNumberWhatsapp() : null;
    // ⚠️ Retorna null, que se usa sin validación posterior
}
```

**Severidad:** 🟠 MODERADO
**Impacto:** null propagado a servicios WhatsApp

---

### 8. JwtRequestFilter.java - Optional messageService

**Ubicación:** `src/main/java/com/restaurante/bot/util/JwtRequestFilter.java:30, 63`

**Problema:**
```java
@Autowired(required = false)
private ErrorMessageService messageService;

// Línea 63:
response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
    messageService.getMessage("session.invalid"));  // ❌ NPE si messageService es null
```

**Severidad:** 🟠 MODERADO
**Contexto:** Afecta tests @WebMvcTest

**Solución Propuesta:**
```java
if (messageService != null) {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
        messageService.getMessage("session.invalid"));
} else {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session invalid");
}
```

---

## ✅ Acciones Recomendadas

### Inmediatas (1-2 días)
1. ✅ Corregir JwtUtil - Validar claims no nulos
2. ✅ Corregir JwtRequestFilter - Validar antes de usar
3. ✅ Corregir OrderDetailsService - Company null check
4. ✅ Corregir OrderDetailsDeliveryService - Customer null check

### A Corto Plazo (1 semana)
5. ✅ Agregar @NonNull de Lombok
6. ✅ Implementar validación de tokens
7. ✅ Tests unitarios para casos null

### Permanente
8. ✅ Code review checklist para null safety
9. ✅ Static analysis tools (SpotBugs, Checker Framework)
10. ✅ Usar Optional<> en retornos

---

## 📋 Checklist de Verificación

- [ ] Actualizar JwtUtilUser.java con null checks
- [ ] Actualizar JwtUtil.java con validaciones
- [ ] Actualizar JwtRequestFilter.java con manejo seguro
- [ ] Actualizar OrderDetailsService.java
- [ ] Actualizar OrderDetailsDeliveryService.java
- [ ] Agregar logs DEBUG
- [ ] Ejecutar suite de tests
- [ ] Realizar prueba de integración

---

## 📝 Referencias

- **FIX_SUMMARY.md** - Problemas previos resueltos
- **test_output.txt** - Resultados de ejecución de tests
- **JwtUtil.java** - Componente crítico de autenticación
- **CustomExceptionHandler.java** - Manejo de excepciones

---

**Analista:** GitHub Copilot  
**Fecha de Análisis:** 2026-04-10  
**Versión:** 1.0
