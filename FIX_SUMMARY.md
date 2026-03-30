# Oracle Database Migration - Error Fixes

## Summary
Your application has been experiencing **two distinct issues** that have now been resolved:

### Issue 1: ORA-00933 - SQL Command Not Properly Ended
**Root Cause**: Application was configured to use MySQL dialect but connecting to Oracle Database. Hibernate was generating MySQL's `LIMIT` pagination syntax which Oracle doesn't support.

**Symptoms**:
- Multiple errors with SQL queries containing `limit ?,?`
- Error: `ORA-00933: SQL command not properly ended`
- Queries failing for `Company`, `Rol`, and other entities

**Solution Applied**:
1. Created `application-prod.yml` with Oracle-specific configuration
2. Set Hibernate dialect to `org.hibernate.dialect.Oracle21Dialect`
3. Updated `app.db.type` to `oracle` in production
4. Configured datasource driver to `oracle.jdbc.OracleDriver`

**Files Modified**:
- ✅ Created: `src/main/resources/application-prod.yml`
- ✅ Updated: `src/main/resources/application.yml` (main configuration)

**For Production Deployment**: Set these environment variables:
```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@<host>:<port>:<SID>
SPRING_DATASOURCE_DRIVER=oracle.jdbc.OracleDriver
SPRING_DATASOURCE_USERNAME=<your_oracle_user>
SPRING_DATASOURCE_PASSWORD=<your_password>
DB_TYPE=oracle
DB_DIALECT=org.hibernate.dialect.Oracle21Dialect
```

---

### Issue 2: EntityNotFoundException - Company with ID 33 Not Found
**Root Cause**: When a User entity references a Company via lazy-loading proxy, accessing the Company properties triggers database query. If the Company doesn't exist, Hibernate throws `EntityNotFoundException`.

**Symptoms**:
- Error: `Unable to find com.restaurante.bot.model.Company with id 33`
- Occurred during login process
- Stack trace showed lazy-loading initialization failure

**Solution Applied**:
Added error handling and validation in `LoginGGPServiceImpl.login()`:
- Wrapped Company access in try-catch block
- Validate that Company exists before using its properties
- Return user-friendly error message if Company not found
- Validate that user has a valid company assigned

**File Modified**:
- ✅ Updated: `src/main/java/com/restaurante/bot/business/service/LoginGGPServiceImpl.java`

---

## Database Configuration Files

### ORM Mapping Files
The application uses ORM mapping extensions specific to database type:
- ✅ `src/main/resources/META-INF/orm-mysql.xml` - MySQL-specific mappings
- ✅ `src/main/resources/META-INF/orm-oracle.xml` - Oracle-specific mappings

These are selected dynamically based on the `app.db.type` configuration.

---

## Testing Recommendations

### 1. Verify Oracle Connections
```bash
# Test datasource connectivity
curl -X GET http://localhost:8080/api/back-whatsapp-qr-app/companies/all
```

### 2. Test Login Flow
```bash
# Test login endpoint
curl -X POST http://localhost:8080/api/back-whatsapp-qr-app/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"your_password"}'
```

### 3. Database Validation
Ensure in your Oracle database:
```sql
-- Check Company table
SELECT * FROM company WHERE status = 'ACTIVE';

-- Check User-Company relationships
SELECT u.user_id, u.name, c.id, c.external_company_id 
FROM user_table u
JOIN company c ON u.company_id = c.id
WHERE u.status = 'ACTIVE';

-- Verify Company ID 33 exists (if referenced)
SELECT * FROM company WHERE id = 33;
```

---

## Environment-Specific Profiles

The application now properly supports three profiles:

### Local Development (`local` profile)
```yaml
datasource: H2 in-memory or local development database
driver: H2Driver or MySQL
dialect: MySQL8Dialect
```

### Testing (`test` profile)
```yaml
datasource: H2 in-memory
driver: H2Driver
dialect: MySQL8Dialect
```

### Production (`prod` profile) - NEW
```yaml
datasource: Oracle Database
driver: oracle.jdbc.OracleDriver
dialect: Oracle21Dialect
```

---

## Next Steps

1. **Deploy Production Configuration**
   - Update your deployment pipeline to set `SPRING_PROFILES_ACTIVE=prod`
   - Configure Oracle datasource environment variables

2. **Data Cleanup** (if needed)
   - Check for orphaned Company/User references
   - Ensure all active Users have valid Companies assigned

3. **Monitoring**
   - Enable Hibernate SQL logging: `logging.level.org.hibernate.SQL: DEBUG`
   - Monitor database connections and pool utilization
   - Check application logs for any `EntityNotFoundException`

4. **Backup**
   - Before running in production, backup Oracle database
   - Verify ORM mappings work correctly with your Oracle schema

---

## Relevant Classes

- `LoginGGPServiceImpl` - Login service with Company validation
- `CompanyApplicationService` - Company management
- `RolServiceImpl` - Role management
- Application configuration classes in `src/main/resources/`

---

## Need Help?

If you encounter any issues:
1. Check application logs for detailed error messages
2. Verify Oracle datasource connectivity
3. Ensure all users/companies exist in database
4. Check for database constraint violations
5. Validate JPA/Hibernate ORM mappings for Oracle
