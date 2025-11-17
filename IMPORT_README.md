# 📦 Guía de Exportación e Importación de Jenkins

Esta guía te permite exportar tu instalación completa de Jenkins con todos los datos, jobs, plugins y configuraciones para transferirla a otra PC.

---

## 🚀 Exportar desde PC Actual

### 1. Ejecutar el script de exportación

```powershell
.\export-docker.ps1
```

Este script exportará:
- ✅ **Imagen de Jenkins** (jenkins/jenkins:2.528.2-lts-jdk21)
- ✅ **Datos de Jenkins** (jobs, plugins, configuración, credenciales)
- ✅ **Historial de builds**
- ✅ **Configuración de puertos** (8080, 8081, 50000, 3000)

### 2. Resultado

Se creará una carpeta `docker-export/` con:
```
docker-export/
├── jenkins-image.tar           # Imagen de Jenkins (~500-800 MB)
├── jenkins-home.tar.gz         # Todos los datos de Jenkins
└── config.json                 # Configuración de puertos
```

### 3. Tamaño aproximado

- Jenkins imagen: ~500-800 MB
- Jenkins datos: Variable (depende de cuántos jobs y builds tengas)
- **Total: ~1-2 GB**

---

## 📥 Importar en Nueva PC

### Requisitos previos

1. **Docker Desktop instalado** y corriendo
2. **PowerShell** (viene con Windows)
3. Carpeta `docker-export/` copiada desde la PC original

### Pasos de importación

#### 1. Copiar archivos

Copia toda la carpeta `docker-export/` y el archivo `import-docker.ps1` al mismo directorio en la nueva PC.

Estructura de archivos:
```
C:\ruta\tu\proyecto\
├── docker-export/
│   ├── jenkins-image.tar
│   ├── jenkins-home.tar.gz
│   └── config.json
└── import-docker.ps1
```

#### 2. Ejecutar el script de importación

```powershell
.\import-docker.ps1
```

El script automáticamente:
1. Cargará la imagen de Jenkins
2. Creará el volumen necesario
3. Restaurará todos los datos
4. Configurará el contenedor con los puertos correctos
5. Instalará y configurará Docker CLI en Jenkins
6. Iniciará Jenkins

#### 3. Verificar que todo funciona

Una vez completada la importación, verifica que Jenkins esté corriendo:

```powershell
docker ps
```

Deberías ver el contenedor de Jenkins corriendo con los puertos:
- 8080 (Jenkins UI)
- 8081 (Auxiliar)
- 50000 (Agentes)
- 3000 (Auxiliar)

---

## 🌐 Acceder a Jenkins

Después de la importación exitosa:

**Jenkins:** http://localhost:8080

---

## 🔐 Datos preservados

✅ **Todos los jobs y pipelines**
✅ **Plugins instalados**
✅ **Credenciales y configuración**
✅ **Historial de builds**
✅ **Usuarios configurados**
✅ **Configuración de Docker CLI**
✅ **Acceso al Docker socket**

---

## ⚠️ Notas importantes

### Compatibilidad
- ✅ Funciona en cualquier PC con **Docker Desktop instalado**
- ✅ Compatible con **Windows 10/11**
- ⚠️ Asegúrate de tener al menos **5 GB de espacio libre**

### Puertos
Los siguientes puertos deben estar libres en la nueva PC:
- **8080** - Jenkins UI
- **8081** - Jenkins (auxiliar)
- **8082** - Backend API
- **3001** - Frontend
- **3000** - Jenkins (auxiliar)
- **50000** - Jenkins (agentes)

### Volúmenes Docker
Se crearán automáticamente dos volúmenes:
- `jenkins-home-vol` - Datos de Jenkins
- `backend-data-vol` - Base de datos del backend

Estos volúmenes persisten incluso si eliminas los contenedores.

---

## 🛠️ Solución de problemas

### Error: "Puerto ya en uso"
```powershell
# Verifica qué está usando el puerto
netstat -ano | findstr :8080

# Detén el proceso o cambia el puerto en el script
```

### Error: "No se encontró docker-export"
```
Asegúrate de que la carpeta docker-export/ esté en el mismo directorio que import-docker.ps1
```

### Contenedores no inician
```powershell
# Ver logs del contenedor
docker logs jenkins_container_name
docker logs e4-backend
docker logs e4-frontend

# Reiniciar contenedor específico
docker restart nombre_contenedor
```

### Jenkins necesita configuración inicial
Si Jenkins pide contraseña inicial:
```powershell
# Obtener contraseña inicial de Jenkins
docker exec nombre_jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

---

## 🔄 Actualizar datos

Si necesitas actualizar los datos en la PC nueva:

1. En la PC original, ejecuta nuevamente:
   ```powershell
   .\export-docker.ps1
   ```

2. Copia solo los archivos de datos:
   - `jenkins-home.tar.gz`
   - `backend-data.tar.gz`

3. En la PC nueva, detén los contenedores:
   ```powershell
   docker stop nombre_jenkins e4-backend e4-frontend
   ```

4. Restaura los datos y reinicia:
   ```powershell
   .\import-docker.ps1
   ```

---

## 📞 Soporte

Si encuentras problemas:
1. Revisa los logs: `docker logs nombre_contenedor`
2. Verifica el estado: `docker ps -a`
3. Consulta `docker-export/EXPORT_INFO.txt` para detalles de la exportación

---

## 🎯 Checklist rápido

### Antes de exportar:
- [ ] Todos los contenedores están corriendo
- [ ] Has guardado todos los cambios importantes
- [ ] Tienes espacio suficiente en disco

### En la nueva PC:
- [ ] Docker Desktop instalado y corriendo
- [ ] PowerShell disponible
- [ ] Puertos necesarios libres
- [ ] Al menos 5 GB de espacio libre

### Después de importar:
- [ ] Todos los contenedores corriendo (`docker ps`)
- [ ] Jenkins accesible en http://localhost:8080
- [ ] Backend accesible en http://localhost:8082
- [ ] Frontend accesible en http://localhost:3001
- [ ] Datos preservados (usuarios, videos, jobs)

---

¡Listo! Tu entorno Docker completo ha sido migrado exitosamente. 🎉
