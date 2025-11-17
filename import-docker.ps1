# Script de importacion de Docker
Write-Host "Importando configuracion completa..." -ForegroundColor Green

$exportDir = "docker-export"
if (-not (Test-Path $exportDir)) { Write-Host "ERROR: No se encontro docker-export/" -ForegroundColor Red; exit 1 }

$config = Get-Content "$exportDir/config.json" | ConvertFrom-Json

Write-Host "Cargando imagenes..." -ForegroundColor Cyan
docker load -i "$exportDir/jenkins-image.tar"
docker load -i "$exportDir/e4-backend-image.tar"
docker load -i "$exportDir/e4-frontend-image.tar"

Write-Host "Deteniendo contenedores existentes..." -ForegroundColor Cyan
docker stop $config.jenkins.container_name 2>$null; docker rm $config.jenkins.container_name 2>$null
docker stop e4-backend 2>$null; docker rm e4-backend 2>$null
docker stop e4-frontend 2>$null; docker rm e4-frontend 2>$null

Write-Host "Creando volumenes..." -ForegroundColor Cyan
docker volume create jenkins-home-vol 2>$null
docker volume create backend-data-vol 2>$null

Write-Host "Restaurando datos..." -ForegroundColor Cyan
if (Test-Path "$exportDir/jenkins-home.tar.gz") {
    docker run --rm -v jenkins-home-vol:/var/jenkins_home -v "${PWD}/${exportDir}:/backup" alpine sh -c "cd / && tar xzf /backup/jenkins-home.tar.gz"
}
if (Test-Path "$exportDir/backend-data.tar.gz") {
    docker run --rm -v backend-data-vol:/app/data -v "${PWD}/${exportDir}:/backup" alpine sh -c "cd / && tar xzf /backup/backend-data.tar.gz"
}

Write-Host "Iniciando contenedores..." -ForegroundColor Cyan
docker run -d --name $config.jenkins.container_name -p 8080:8080 -p 8081:8081 -p 50000:50000 -p 3000:3000 -v jenkins-home-vol:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock $config.jenkins.image
Start-Sleep -Seconds 5
docker exec -u 0 $config.jenkins.container_name bash -c "apt-get update && apt-get install -y docker.io" 2>$null
docker exec -u 0 $config.jenkins.container_name bash -c "usermod -aG docker jenkins; chmod 666 /var/run/docker.sock" 2>$null

docker run -d --name e4-backend -p 8082:8081 -v backend-data-vol:/app/data e4-backend:latest
docker run -d --name e4-frontend -p 3001:3000 e4-frontend:latest

Write-Host "IMPORTACION COMPLETADA" -ForegroundColor Green
Write-Host "Jenkins: http://localhost:8080" -ForegroundColor White
Write-Host "Backend: http://localhost:8082" -ForegroundColor White
Write-Host "Frontend: http://localhost:3001" -ForegroundColor White
