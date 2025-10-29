pipeline {
    agent any

    environment {
    PROJECT_NAME = 'JdrBibli'
    MAVEN_HOME = tool 'maven-3.9.9'
}

    stages {

        stage('Checkout') {
            steps {
                echo "🔄 Clonage du dépôt GitHub..."
                checkout scm
            }
        }

        stage('Build & Test (Maven)') {
            steps {
                echo "🏗️ Compilation et exécution des tests unitaires..."
                sh "mvn -B clean install -DskipTests=false"
            }
        }

        stage('Docker Build') {
            steps {
                echo "🐳 Construction des images Docker..."
                script {
                    def services = ['auth-service', 'user-service', 'ouvrage-service', 'audit-service', 'gateway']
                    for (svc in services) {
                        sh "docker build -t ${svc}:latest ${svc}"
                    }
                }
            }
        }

        stage('Docker Compose Up') {
            steps {
                echo "🚀 Lancement des conteneurs (docker-compose up)..."
                sh "docker compose up -d"
            }
        }
    }

    post {
        success {
            echo "✅ Build et déploiement réussis pour ${PROJECT_NAME}"
        }
        failure {
            echo "❌ Erreur durant le pipeline, vérifie les logs Jenkins."
        }
    }
}
