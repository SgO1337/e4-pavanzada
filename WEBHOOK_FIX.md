# Solución al Error 403 - GitHub Webhook

## El Problema
GitHub está enviando webhooks pero Jenkins los rechaza con:
```
HTTP ERROR 403 No valid crumb was included in the request
```

Esto es por la protección CSRF de Jenkins.

## Solución 1: Configurar Authentication Token (RECOMENDADO)

### Paso 1: Crear un Token en Jenkins
1. Ve a Jenkins: http://localhost:8080
2. Click en tu usuario (arriba derecha) → **Configure**
3. Busca la sección **API Token**
4. Click en **Add new Token**
5. Nombre: `github-webhook`
6. Click **Generate**
7. **COPIA EL TOKEN** (solo se muestra una vez)

### Paso 2: Actualizar el Webhook en GitHub
1. Ve a tu repo: https://github.com/SgO1337/e4-pavanzada/settings/hooks
2. Click en el webhook existente
3. Actualiza la **Payload URL** a:
   ```
   https://TU_USUARIO:TU_TOKEN@tu-url-ngrok.ngrok.io/github-webhook/
   ```
   
   Ejemplo:
   ```
   https://admin:11a1234567890abcdef@abc123.ngrok.io/github-webhook/
   ```

4. **Secret**: Déjalo vacío
5. **Content type**: application/json
6. **Which events**: Just the push event
7. Click **Update webhook**

### Paso 3: Verificar
1. Haz un commit y push
2. En GitHub → Settings → Webhooks → Recent Deliveries
3. Deberías ver un ✓ verde (200 OK)

---

## Solución 2: Desactivar CSRF para GitHub Webhook (ALTERNATIVA)

⚠️ **Menos seguro, solo para desarrollo**

### Opción A: Instalar GitHub Plugin Correctamente

1. En Jenkins: **Manage Jenkins** → **Manage Plugins**
2. Busca: **GitHub Plugin**
3. Instálalo si no está
4. **Manage Jenkins** → **Configure System**
5. Busca sección **GitHub**
6. Click **Add GitHub Server**
7. Marca **Override Hook URL**: Déjalo vacío
8. En **Advanced**:
   - Marca **Specify another hook URL for GitHub configuration**
   - URL: `http://localhost:8080/github-webhook/`
9. Save

### Opción B: Usar URL Alternativa

Cambia el webhook en GitHub a usar:
```
https://tu-url-ngrok.ngrok.io/git/notifyCommit?url=https://github.com/SgO1337/e4-pavanzada
```

Esta URL no requiere CSRF token.

---

## Solución 3: Poll SCM (MÁS SIMPLE, NO REQUIERE WEBHOOK)

Si nada funciona, usa polling en lugar de webhooks:

### En el Pipeline de Jenkins:
1. Ve a tu pipeline → **Configure**
2. En **Build Triggers**:
   - ✓ **Poll SCM**
   - Schedule: `H/2 * * * *` (cada 2 minutos)
3. Save

Jenkins verificará GitHub cada 2 minutos automáticamente.

**Ventajas:**
- ✓ No requiere ngrok
- ✓ No requiere webhook
- ✓ Funciona local sin configuración

**Desventajas:**
- ✗ No es instantáneo (espera hasta 2 min)
- ✗ Más carga en GitHub

---

## Mi Recomendación

**Para desarrollo local:** Usa **Solución 3 (Poll SCM)**
- Es la más simple y confiable
- No requiere ngrok corriendo todo el tiempo
- No hay problemas de CSRF

**Para producción:** Usa **Solución 1 (Token Authentication)**
- Más seguro
- Inmediato
- Profesional

---

## Verificar que Funciona

Después de aplicar cualquier solución:

1. Haz un cambio en el código
2. Commit y push:
   ```bash
   git add .
   git commit -m "Test webhook fix"
   git push
   ```
3. Revisa Jenkins dashboard
4. Deberías ver un nuevo build iniciarse automáticamente

