pipeline {
    agent any
    
    tools {
        maven 'Maven'
        nodejs 'NodeJS'
    }
    
    environment {
        BACKEND_DIR = 'backend'
        FRONTEND_DIR = 'frontend'
        BACKEND_PORT = '8081'
        FRONTEND_PORT = '3000'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from GitHub...'
                checkout scm
            }
        }
        
        stage('Build Backend') {
            steps {
                echo 'Building Spring Boot backend...'
                dir("${BACKEND_DIR}") {
                    script {
                        if (isUnix()) {
                            sh 'mvn clean package -DskipTests'
                        } else {
                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
            }
        }
        
        stage('Build Frontend') {
            steps {
                echo 'Building Next.js frontend...'
                dir("${FRONTEND_DIR}") {
                    script {
                        if (isUnix()) {
                            sh 'npm install'
                            sh 'npm run build'
                        } else {
                            bat 'npm install'
                            bat 'npm run build'
                        }
                    }
                }
            }
        }
        
        stage('Deploy Backend') {
            steps {
                echo 'Deploying Spring Boot backend...'
                dir("${BACKEND_DIR}") {
                    script {
                        if (isUnix()) {
                            sh '''
                                # Kill existing backend process if running
                                pkill -f 'backend-0.0.1-SNAPSHOT.jar' || true
                                # Start backend in background
                                nohup java -jar target/backend-0.0.1-SNAPSHOT.jar --server.port=${BACKEND_PORT} > backend.log 2>&1 &
                                echo $! > backend.pid
                            '''
                        } else {
                            bat '''
                                @echo off
                                REM Kill existing backend process if running
                                for /f "tokens=5" %%a in ('netstat -aon ^| find ":8081" ^| find "LISTENING"') do taskkill /F /PID %%a 2>nul
                                REM Start backend in background
                                start /B java -jar target\\backend-0.0.1-SNAPSHOT.jar --server.port=%BACKEND_PORT%
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Deploy Frontend') {
            steps {
                echo 'Deploying Next.js frontend...'
                dir("${FRONTEND_DIR}") {
                    script {
                        if (isUnix()) {
                            sh '''
                                # Kill existing frontend process if running
                                pkill -f 'next start' || true
                                # Start frontend in background
                                nohup npm start > frontend.log 2>&1 &
                                echo $! > frontend.pid
                            '''
                        } else {
                            bat '''
                                @echo off
                                REM Kill existing frontend process if running
                                for /f "tokens=5" %%a in ('netstat -aon ^| find ":3000" ^| find "LISTENING"') do taskkill /F /PID %%a 2>nul
                                REM Start frontend in background
                                start /B npm start
                            '''
                        }
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline completed successfully!'
            echo 'Backend is running on http://localhost:8081'
            echo 'Frontend is running on http://localhost:3000'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
