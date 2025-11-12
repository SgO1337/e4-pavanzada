# Jenkins CI/CD Pipeline Setup Guide

## Overview
This project uses Jenkins for automated CI/CD. When you push to GitHub, Jenkins automatically:
1. Checks out the latest code
2. Builds the Spring Boot backend
3. Builds the Next.js frontend
4. Deploys both applications locally

## Prerequisites
- Jenkins running in Docker at http://localhost:8080/
- Java 17 (for backend)
- Node.js (will be installed by Jenkins)
- Maven (will be installed by Jenkins)

## Deployed Applications
- **Backend**: http://localhost:8081
- **Frontend**: http://localhost:3000
- **Jenkins**: http://localhost:8080

## Setup Steps

### 1. Install Jenkins Plugins
Go to **Manage Jenkins** → **Manage Plugins** → **Available**:
- Git Plugin
- GitHub Plugin
- Maven Integration Plugin
- NodeJS Plugin
- Pipeline Plugin

### 2. Configure Global Tools
Go to **Manage Jenkins** → **Global Tool Configuration**:

**Maven**:
- Name: `Maven`
- Install automatically: ✓
- Version: 3.9.5 or later

**NodeJS**:
- Name: `NodeJS`
- Install automatically: ✓
- Version: 18.x or later

### 3. Create Pipeline Job
1. **New Item** → Name: `e4-pavanzada-pipeline` → **Pipeline**
2. Configure:
   - **GitHub project**: https://github.com/SgO1337/e4-pavanzada
   - **Build Triggers**: GitHub hook trigger for GITScm polling
   - **Pipeline**:
     - Definition: Pipeline script from SCM
     - SCM: Git
     - Repository URL: https://github.com/SgO1337/e4-pavanzada.git
     - Branch: */main
     - Script Path: Jenkinsfile

### 4. GitHub Webhook (Optional)

#### Option A: Using ngrok (for local development)
1. Download and install ngrok: https://ngrok.com/
2. Run: `ngrok http 8080`
3. Copy the ngrok URL (e.g., https://xxxx.ngrok.io)
4. In GitHub repo → **Settings** → **Webhooks** → **Add webhook**:
   - Payload URL: `https://xxxx.ngrok.io/github-webhook/`
   - Content type: application/json
   - Events: Just the push event

#### Option B: Manual Trigger
- Go to Jenkins dashboard
- Click on your pipeline job
- Click **Build Now**

### 5. Test the Pipeline
1. Make a change to your code
2. Commit and push to GitHub:
   ```bash
   git add .
   git commit -m "Test Jenkins pipeline"
   git push origin main
   ```
3. Check Jenkins dashboard to see the build progress
4. Once complete, verify:
   - Backend: http://localhost:8081/hello
   - Frontend: http://localhost:3000

## Troubleshooting

### Jenkins can't find Maven/NodeJS
- Check **Manage Jenkins** → **Global Tool Configuration**
- Ensure Maven and NodeJS are configured with names matching the Jenkinsfile

### Build fails on Windows
- Ensure Java 17 is installed and JAVA_HOME is set
- Check that ports 8081 and 3000 are not already in use

### Backend/Frontend won't deploy
- Check Jenkins build logs
- Manually stop processes on ports 8081 and 3000:
  ```powershell
  # Find and kill process on port 8081
  Get-Process -Id (Get-NetTCPConnection -LocalPort 8081).OwningProcess | Stop-Process -Force
  # Find and kill process on port 3000
  Get-Process -Id (Get-NetTCPConnection -LocalPort 3000).OwningProcess | Stop-Process -Force
  ```

### GitHub webhook not triggering
- Ensure ngrok is running if using it
- Check webhook delivery in GitHub Settings → Webhooks
- Verify Jenkins URL is accessible from internet
- Alternative: Use **Poll SCM** in Jenkins (Build Triggers → Poll SCM: `H/5 * * * *`)

### Plugin install fails with `UnknownHostException: updates.jenkins.io`
This is a DNS/connectivity issue inside the Jenkins Docker container.

Quick diagnostics (PowerShell):
```powershell
# 1) Find your Jenkins container name/ID
docker ps --format "table {{.ID}}\t{{.Names}}\t{{.Ports}}"

# 2) Test DNS resolution from inside the container
docker exec <JENKINS_CONTAINER_NAME> getent hosts updates.jenkins.io

# 3) Test HTTPS connectivity
docker exec <JENKINS_CONTAINER_NAME> bash -lc "curl -I https://updates.jenkins.io/download/plugins/nodejs/1.6.5/nodejs.hpi"
```

If step 2 fails (no IPs returned):
- Docker Desktop → Settings → Docker Engine → add/merge:
   ```json
   {
      "dns": ["8.8.8.8", "1.1.1.1"]
   }
   ```
   Then click Apply & Restart.

Or recreate the container with explicit DNS servers:
```powershell
docker stop Jenkins; docker rm Jenkins
docker run -d --name Jenkins -p 8080:8080 -p 50000:50000 --restart unless-stopped `
   --dns 8.8.8.8 --dns 1.1.1.1 -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
```

If step 2 works but step 3 fails (corporate proxy):
- Configure proxy in Jenkins: Manage Jenkins → Plugins → Advanced → Proxy Configuration
- Or run container with proxy vars:
   ```powershell
   docker run -d --name Jenkins -p 8080:8080 -p 50000:50000 --restart unless-stopped `
      -e http_proxy=http://USER:PASS@proxy:PORT -e https_proxy=http://USER:PASS@proxy:PORT `
      -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
   ```

Manual fallback: Upload plugin .hpi
- Download on host: https://updates.jenkins.io/download/plugins/nodejs/1.6.5/nodejs.hpi
- Jenkins → Manage Jenkins → Plugins → Advanced → Upload plugin → Select the .hpi → Upload → Restart

## Manual Deployment

If you need to deploy manually without Jenkins:

### Backend
```bash
cd backend
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar --server.port=8081
```

### Frontend
```bash
cd frontend
npm install
npm run build
npm start
```

## Stopping Applications

### Windows PowerShell
```powershell
# Stop backend (port 8081)
Get-Process -Id (Get-NetTCPConnection -LocalPort 8081).OwningProcess | Stop-Process -Force

# Stop frontend (port 3000)
Get-Process -Id (Get-NetTCPConnection -LocalPort 3000).OwningProcess | Stop-Process -Force
```

## Notes
- The pipeline uses background processes to keep apps running after Jenkins completes
- Logs are available in Jenkins build console output
- Each new deployment kills previous processes automatically
