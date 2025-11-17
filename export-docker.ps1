# Script de exportacion de Docker
Write-Host "Exportando configuracion completa..." -ForegroundColor Green

$exportDir = "docker-export"
if (Test-Path $exportDir) { Remove-Item -Recurse -Force $exportDir }
New-Item -ItemType Directory -Path $exportDir | Out-Null

Write-Host "Exportando imagenes..." -ForegroundColor Cyan
docker save -o "$exportDir/jenkins-image.tar" jenkins/jenkins:2.528.2-lts-jdk21
docker save -o "$exportDir/e4-backend-image.tar" e4-backend:latest
docker save -o "$exportDir/e4-frontend-image.tar" e4-frontend:latest

Write-Host "Exportando datos de Jenkins..." -ForegroundColor Cyan
$jenkinsContainer = docker ps -a --filter "ancestor=jenkins/jenkins:2.528.2-lts-jdk21" --format "{{.Names}}"
if ($jenkinsContainer) {
    docker run --rm --volumes-from $jenkinsContainer -v "${PWD}/${exportDir}:/backup" alpine tar czf /backup/jenkins-home.tar.gz /var/jenkins_home
}

Write-Host "Exportando datos del backend..." -ForegroundColor Cyan
if (docker ps -a --format "{{.Names}}" | Select-String -Pattern "^e4-backend$") {
    docker run --rm --volumes-from e4-backend -v "${PWD}/${exportDir}:/backup" alpine tar czf /backup/backend-data.tar.gz /app/data
}

Write-Host "Guardando configuracion..." -ForegroundColor Cyan
$config = @{ jenkins = @{ image = "jenkins/jenkins:2.528.2-lts-jdk21"; container_name = $jenkinsContainer; ports = @("8080:8080", "8081:8081", "50000:50000", "3000:3000") }; backend = @{ image = "e4-backend:latest"; container_name = "e4-backend"; ports = @("8082:8081") }; frontend = @{ image = "e4-frontend:latest"; container_name = "e4-frontend"; ports = @("3001:3000") } }
$config | ConvertTo-Json -Depth 10 | Out-File "$exportDir/config.json"

Write-Host "EXPORTACION COMPLETADA" -ForegroundColor Green
Write-Host "Archivos en: $exportDir" -ForegroundColor Yellow
