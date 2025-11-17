# Script de exportacion de Jenkins
Write-Host "Exportando Jenkins..." -ForegroundColor Green

$exportDir = "docker-export"
if (Test-Path $exportDir) { Remove-Item -Recurse -Force $exportDir }
New-Item -ItemType Directory -Path $exportDir | Out-Null

Write-Host "Exportando imagen de Jenkins..." -ForegroundColor Cyan
docker save -o "$exportDir/jenkins-image.tar" jenkins/jenkins:2.528.2-lts-jdk21

Write-Host "Exportando datos de Jenkins..." -ForegroundColor Cyan
$jenkinsContainer = docker ps -a --filter "ancestor=jenkins/jenkins:2.528.2-lts-jdk21" --format "{{.Names}}"
if ($jenkinsContainer) {
    Write-Host "Contenedor encontrado: $jenkinsContainer" -ForegroundColor Yellow
    docker run --rm --volumes-from $jenkinsContainer -v "${PWD}/${exportDir}:/backup" alpine tar czf /backup/jenkins-home.tar.gz /var/jenkins_home
    Write-Host "Datos exportados correctamente" -ForegroundColor Green
} else {
    Write-Host "ERROR: No se encontro contenedor de Jenkins" -ForegroundColor Red
    exit 1
}

Write-Host "Guardando configuracion..." -ForegroundColor Cyan
$config = @{ 
    jenkins = @{ 
        image = "jenkins/jenkins:2.528.2-lts-jdk21"
        container_name = $jenkinsContainer
        ports = @("8080:8080", "8081:8081", "50000:50000", "3000:3000")
    }
}
$config | ConvertTo-Json -Depth 10 | Out-File "$exportDir/config.json"

Write-Host ""
Write-Host "EXPORTACION COMPLETADA" -ForegroundColor Green
Write-Host "Archivos en: $exportDir" -ForegroundColor Yellow
Write-Host "- jenkins-image.tar" -ForegroundColor White
Write-Host "- jenkins-home.tar.gz (jobs, plugins, config)" -ForegroundColor White
Write-Host "- config.json" -ForegroundColor White
