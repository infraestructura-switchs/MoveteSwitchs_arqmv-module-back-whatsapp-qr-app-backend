# 🔍 Índice de Problemas por Archivo

## Archivos con Problemas de NullPointerException

### 1. 🔴 src/main/java/com/restaurante/bot/security/JwtUtilUser.java

**Problemas:** 2 Críticos

```
Línea 54: return extractExpiration(token).before(new Date());
         └─ NPE si extractExpiration retorna null
         
Línea 59: return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
         └─ NPE si username es null
```

**Impacto:** Validación de token bloqueada
**Prioridad:** 🔴 MÁXIMA

---

### 2. 🔴 src/main/java/com/restaurante/bot/util/JwtUtil.java

**Problemas:** 3 Críticos

```
Línea 65: return extractClaim(token, claims -> claims.get("externalCompanyId", Long.class));
         └─ Puede retornar null
         
Línea 69: return extractClaim(token, claims -> claims.get("sessionId", String.class));
         └─ Puede retornar null
         
Línea 73: return extractClaim(token, claims -> claims.get("userId", Long.class));
         └─ Puede retornar null
```

**Impacto:** Tokens sin claims requeridos causan cascada de fallos
**Prioridad:** 🔴 MÁXIMA

---

### 3. 🔴 src/main/java/com/restaurante/bot/util/JwtRequestFilter.java

**Problemas:** 2 (1 Crítico, 1 Moderado)

```
Línea 48: Claims claims = jwtUtil.extractAllClaims(token);
         └─ El siguiente uso sin null-check de campos

Línea 54: Long externalCompanyId = jwtUtil.extractExternalCompanyId(token);  // Puede ser null
         └─ NPE si externalCompanyId es null después

Línea 55: String sessionId = jwtUtil.extractSessionId(token);                // Puede ser null
         └─ Lógica comprometida con null

Línea 63: response.sendError(..., messageService.getMessage(...));
         └─ NPE si messageService es null (required=false)
         
Línea 66: List<String> authorities = (List<String>) claims.get("authorities");
         └─ Cast inseguro sin validación
```

**Impacto:** Falla en procesamiento de tokens válidos
**Prioridad:** 🔴 MÁXIMA

---

### 4. 🔴 src/main/java/com/restaurante/bot/business/service/OrderDetailsService.java

**Problemas:** 1 Crítico

```
Línea 668: Company company = companyRepository.findByExternalCompanyId(tokenCompanyId);
          log.info("confirmedOreders..., companyId={}", company.getId());
         └─ NPE si company es null
         
Línea 677: List<Object[]> resultList = orderTransactionRepository.findAllOrdersConfirmJPQL(
            tableNumber, phoneNumber, company.getId());
         └─ company.getId() cuando company puede ser null
```

**Métodos afectados:**
- confirmedOreders() - Línea 663
- noConfirmationOrder() - Línea 630 (similar)

**Impacto:** Órdenes confirmadas no funcionan
**Prioridad:** 🔴 MÁXIMA

---

### 5. 🔴 src/main/java/com/restaurante/bot/business/service/OrderDetailsDeliveryService.java

**Problemas:** 1 Crítico

```
Línea 168: com.restaurante.bot.model.Customer customer = 
           customerRepository.findById(od.getCustomerId()).orElse(null);

Línea 305-310 (en updateOrder):
          Customer customer = customerRepository.findByPhone(orderDetailsDeliveryDTO.getPhone());
          customer.setTypeIdentificationId(orderDetailsDeliveryDTO.getTypeIdentificationId());
         └─ NPE si customer es null
         
         customer.setNumerIdentification(...);
         customer.setName(...);
         customer.setAddress(...);
         customer.setPhone(...);
         customer.setEmail(...);
         └─ Múltiples NPE potenciales
```

**Métodos afectados:**
- getOrderDetails() - Línea 168
- updateOrder() - Línea 305

**Impacto:** Actualización de órdenes de delivery falla
**Prioridad:** 🔴 MÁXIMA

---

### 6. 🟠 src/main/java/com/restaurante/bot/util/SearchDTOConverter.java

**Problemas:** 1 Moderado (parcialmente protegido)

```
Línea 136: String value = map.get(key);
         └─ Puede ser null

Línea 145: return Integer.parseInt(map.get(key));
         └─ NPE si key no existe (pero hay try-catch)

Línea 156: return Long.parseLong(map.get(key));
         └─ NPE si key no existe (pero hay try-catch)
```

**Impacto:** Búsquedas con parámetros malformados
**Prioridad:** 🟠 MEDIA (parcialmente protegido)

---

### 7. 🟠 src/main/java/com/restaurante/bot/model/Company.java

**Problemas:** 1 Moderado (protegido)

```
Línea 124-130: private CompanyIntegrationDetail integrationDetail() {
               if (integrationDetails == null || integrationDetails.isEmpty()) {
                   return null;
               }
               return integrationDetails.get(0);
              └─ Retorna null legítimamente

Línea 77, 85, 93, 101, 109, 117: getNumberWhatsapp(), getNumberId(), etc.
              return integrationDetail() != null ? integrationDetail().getXxx() : null;
             └─ Ya protegido con null check ternario
```

**Impacto:** Campos de integración null
**Prioridad:** 🟠 MEDIA (ya protegido)

---

## 📊 Resumen de Ubicaciones

| Severidad | # | Archivos | Líneas Críticas |
|-----------|---|----------|-----------------|
| 🔴 CRÍTICO | 8 | 5 archivos | 1, 48, 54, 55, 63, 66, 305, 668 |
| 🟠 MODERADO | 2 | 2 archivos | 136, 145, 156 (parcialmente protegido) |

---

## 🚀 Orden de Reparación Recomendado

### FASE 1 (Máxima Urgencia - 1-2 horas)
1. **JwtUtil.java** (líneas 65, 69, 73)
   - Agregar validaciones de null en extractExternalCompanyId(), extractSessionId(), extractUserId()

2. **JwtRequestFilter.java** (líneas 54, 55, 63)
   - Validar externalCompanyId y sessionId antes de usar
   - Proteger messageService.getMessage();

3. **OrderDetailsService.java** (línea 668)
   - Agregar null check para company

4. **OrderDetailsDeliveryService.java** (línea 305)
   - Agregar null check para customer

### FASE 2 (Alta Urgencia - 1 día)
5. **JwtUtilUser.java** (líneas 54, 59)
   - Agregar try-catch y validaciones nulas

6. **SearchDTOConverter.java** (líneas 145, 156)
   - Mejorar manejo de valores null

---

## 📝 Plantilla de Corrección

Usar esta plantilla para cada corrección:

```java
// ANTES (con NPE):
Type value = repository.find...();
value.method();  // NPE si value es null

// DESPUÉS (seguro):
Type value = repository.find...();
if (value == null) {
    log.warn("Resource not found");
    throw new DomainException(ErrorCode.NOT_FOUND, "descriptive message");
    // O: return null; (si es permitido)
    // O: return Optional.empty(); (si retorna Optional)
}
value.method();  // Seguro, value no es null
```

---

## ✅ Checklist de Verificación

- [ ] Todos los archivos identificados revisados
- [ ] Problemas críticos corregidos
- [ ] Problemas moderados corregidos
- [ ] Suite de tests ejecutada
- [ ] SpotBugs ejecutado (sin nuevas advertencias)
- [ ] Code review completado
- [ ] Cambios desplegados a staging
- [ ] Pruebas de humo ejecutadas
- [ ] Desplegable a producción

---

**Documento Generado:** 2026-04-10  
**Archivos Analizados:** 7 Java + 1 Markdown  
**Total de Problemas:** 10 (8 críticos, 2 moderados)
