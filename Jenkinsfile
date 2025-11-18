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
        BACKEND_PORT = '8082'
        FRONTEND_PORT = '3001'
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
                echo '🔨 Building Spring Boot backend...'
                dir("${BACKEND_DIR}") {
                    script {
                        if (isUnix()) {
                            sh 'mvn clean package -DskipTests'
                        } else {
                            bat 'mvn clean package -DskipTests'
                        }
                    }
                }
                echo 'Backend build completed'
            }
        }
        
        stage('Test Backend') {
            steps {
                echo 'Running Backend Unit Tests...'
                dir("${BACKEND_DIR}") {
                    script {
                        if (isUnix()) {
                            sh 'mvn test'
                        } else {
                            bat 'mvn test'
                        }
                    }
                }
                echo 'All Backend Tests Passed!'
            }
            post {
                always {
                    // Publicar resultados de tests
                    junit "${BACKEND_DIR}/target/surefire-reports/*.xml"
                }
                success {
                    echo 'Tests completed successfully - Proceeding to deployment'
                }
                failure {
                    echo 'Tests failed - Deployment Cancelled!'
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
                echo 'Frontend build completed'
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
                echo 'Backend Docker image built successfully'
            }
        }
        stage('Deploy Backend Container') {
            steps {
                echo 'Deploying backend Docker container...'
                script {
                    sh "docker rm -f e4-backend || true"
                    sh "docker run -d --name e4-backend -p ${BACKEND_PORT}:8081 e4-backend:latest"
                }
                echo 'Backend container deployed on port ${BACKEND_PORT}'
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
                echo 'Frontend Docker image built successfully'
            }
        }
        stage('Deploy Frontend Container') {
            steps {
                echo 'Deploying frontend Docker container...'
                script {
                    sh "docker rm -f e4-frontend || true"
                    sh "docker run -d --name e4-frontend -p ${FRONTEND_PORT}:3000 e4-frontend:latest"
                }
                echo 'Frontend container deployed on port ${FRONTEND_PORT}'
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline completed successfully!'
            echo "Backend is running on http://localhost:${BACKEND_PORT}"
            echo "Frontend is running on http://localhost:${FRONTEND_PORT}"
            // Archive build artifacts so Jenkins keeps them with the run
            archiveArtifacts artifacts: "${BACKEND_DIR}/target/*.jar", fingerprint: true
            archiveArtifacts artifacts: "${FRONTEND_DIR}/.next/**", allowEmptyArchive: true, fingerprint: true
        }
        failure {
            echo 'Pipeline failed!'
            echo 'Check test results or build logs for details'
        }
        always {
            echo 'Cleaning up workspace...'
        }
    }
}
