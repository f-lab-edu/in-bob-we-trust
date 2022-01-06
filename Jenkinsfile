pipeline {
    agent { docker { image 'gradle:7-jdk11' } }
    stages {
        stage('build') {
            steps {
                gradle :delivery-info-service:build
            }
        }
    }
}