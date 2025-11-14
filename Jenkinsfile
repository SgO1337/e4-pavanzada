pipeline {
    agent any
    
    // Trigger on GitHub push (requires GitHub plugin) and as a fallback poll SCM every 5 minutes
    triggers {
        githubPush()
        pollSCM('H/5 * * * *')
    }
    
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
                            // Use npm install because repo has no package-lock.json
                            sh 'npm install'
                            sh 'npm run build'
                        } else {
                            // Use npm install because repo has no package-lock.json
                            bat 'npm install'
                            bat 'npm run build'
                        }
                    }
                }
            }
        }
        
        stage('Build Backend Docker Image') {
            steps {
                echo 'Building backend Docker image...'
                dir("${BACKEND_DIR}") {
                    script {
                        sh "docker build -t e4-backend:latest ."
                    }
                }
            }
        }
        stage('Deploy Backend Container') {
            steps {
                echo 'Deploying backend Docker container...'
                script {
                    sh "docker rm -f e4-backend || true"
                    sh "docker run -d --name e4-backend -p 8081:8081 e4-backend:latest"
                }
            }
        }
        
        stage('Build Frontend Docker Image') {
            steps {
                echo 'Building frontend Docker image...'
                dir("${FRONTEND_DIR}") {
                    script {
                        sh "docker build -t e4-frontend:latest ."
                    }
                }
            }
        }
        stage('Deploy Frontend Container') {
            steps {
                echo 'Deploying frontend Docker container...'
                script {
                    sh "docker rm -f e4-frontend || true"
                    sh "docker run -d --name e4-frontend -p 3000:3000 e4-frontend:latest"
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline completed successfully!'
            echo 'Backend is running on http://localhost:8081'
            echo 'Frontend is running on http://localhost:3000'
            // Archive build artifacts so Jenkins keeps them with the run
            archiveArtifacts artifacts: "${BACKEND_DIR}/target/*.jar", fingerprint: true
            archiveArtifacts artifacts: "${FRONTEND_DIR}/.next/**", allowEmptyArchive: true, fingerprint: true
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
