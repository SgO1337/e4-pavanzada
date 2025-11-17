# Script de importacion de Jenkins
Write-Host "Importando Jenkins..." -ForegroundColor Green

$exportDir = "docker-export"
if (-not (Test-Path $exportDir)) { 
    Write-Host "ERROR: No se encontro docker-export/" -ForegroundColor Red
    Write-Host "Asegurate de copiar la carpeta docker-export a este directorio" -ForegroundColor Yellow
    exit 1 
}

$config = Get-Content "$exportDir/config.json" | ConvertFrom-Json

Write-Host "Cargando imagen de Jenkins..." -ForegroundColor Cyan
docker load -i "$exportDir/jenkins-image.tar"

Write-Host "Deteniendo Jenkins existente (si existe)..." -ForegroundColor Cyan
docker stop $config.jenkins.container_name 2>$null
docker rm $config.jenkins.container_name 2>$null

Write-Host "Creando volumen..." -ForegroundColor Cyan
docker volume create jenkins-home-vol 2>$null

Write-Host "Restaurando datos de Jenkins..." -ForegroundColor Cyan
if (Test-Path "$exportDir/jenkins-home.tar.gz") {
    docker run --rm -v jenkins-home-vol:/var/jenkins_home -v "${PWD}/${exportDir}:/backup" alpine sh -c "cd / && tar xzf /backup/jenkins-home.tar.gz"
    Write-Host "Datos restaurados correctamente" -ForegroundColor Green
} else {
    Write-Host "WARN: No se encontro jenkins-home.tar.gz" -ForegroundColor Yellow
}

Write-Host "Iniciando Jenkins..." -ForegroundColor Cyan
docker run -d `
    --name $config.jenkins.container_name `
    -p 8080:8080 `
    -p 8081:8081 `
    -p 50000:50000 `
    -p 3000:3000 `
    -v jenkins-home-vol:/var/jenkins_home `
    -v /var/run/docker.sock:/var/run/docker.sock `
    $config.jenkins.image

Write-Host "Configurando Docker CLI en Jenkins..." -ForegroundColor Cyan
Start-Sleep -Seconds 5
docker exec -u 0 $config.jenkins.container_name bash -c "apt-get update && apt-get install -y docker.io" 2>$null
docker exec -u 0 $config.jenkins.container_name bash -c "usermod -aG docker jenkins; chmod 666 /var/run/docker.sock" 2>$null

Write-Host ""
Write-Host "IMPORTACION COMPLETADA" -ForegroundColor Green
Write-Host "Jenkins: http://localhost:8080" -ForegroundColor White
Write-Host ""
Write-Host "Todos tus jobs, plugins y configuracion han sido restaurados" -ForegroundColor Cyan
