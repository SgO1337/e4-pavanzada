# Tests del Backend

## Tests Agregados

Se han agregado tests unitarios completos para el backend usando JUnit 5 y Mockito:

### 1. UsuarioServicioTest
- ✅ Registro exitoso de usuario
- ✅ Validación de usuario duplicado
- ✅ Login exitoso
- ✅ Login con contraseña incorrecta
- ✅ Login con usuario inexistente
- ✅ Obtener usuario por ID

### 2. VideoServicioTest
- ✅ Agregar video
- ✅ Extraer YouTube ID (formato watch)
- ✅ Extraer YouTube ID (formato corto)
- ✅ Obtener todos los videos
- ✅ Dar like a un video
- ✅ Quitar like de un video
- ✅ Marcar como favorito
- ✅ Eliminar video (exitoso)
- ✅ Eliminar video (sin permiso)
- ✅ Eliminar video (no encontrado)

### 3. HealthControllerTest
- ✅ Endpoint de health check

## Pipeline CI/CD

El Jenkinsfile ha sido actualizado para:

1. **Build Backend** - Compilar sin ejecutar tests
2. **Test Backend** ⭐ NUEVO - Ejecutar todos los tests
3. **Build Frontend** - Compilar el frontend
4. **Build Docker Images** - Solo si los tests pasan
5. **Deploy Containers** - Solo si todo lo anterior es exitoso

### Flujo del Pipeline

```
Checkout → Build Backend → Test Backend → Build Frontend
    ↓ (solo si tests pasan)
Build Docker Images → Deploy Containers
```

### Resultados de Tests

Jenkins publicará automáticamente los resultados de los tests usando JUnit plugin.
Podrás ver:
- Número de tests ejecutados
- Tests que pasaron/fallaron
- Cobertura de tests
- Historial de tests

## Ejecutar Tests Localmente

```bash
# Desde la carpeta backend
mvn test

# Ver reporte detallado
mvn test && cat target/surefire-reports/*.txt
```

## Tecnologías de Testing

- **JUnit 5** - Framework de testing
- **Mockito** - Mocking framework
- **Spring Boot Test** - Testing utilities para Spring
- **MockMvc** - Testing de controladores REST

## Cobertura

Los tests cubren:
- ✅ Lógica de negocio (servicios)
- ✅ Endpoints REST (controladores)
- ✅ Casos edge (errores, validaciones)
- ✅ Integración con repositorios (mockeados)
