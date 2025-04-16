pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        SONARQUBE_SCANNER = tool 'SonarQubeScanner'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/anvit121/risk-assessment-devsecops-pipeline.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('SonarQube Scan') {
            steps {
                withSonarQubeEnv('SonarQubeScanner') {
                    sh 'sonar-scanner'
                }
            }
        }

        stage('Snyk Scan') {
            steps {
                sh 'snyk test > snyk_results.json'
            }
        }

        stage('Bandit Scan') {
            steps {
                sh 'bandit -r . -f json -o bandit_results.json'
            }
        }

        stage('Terraform Plan & OPA Policy') {
            steps {
                sh '''
                cd terraform
                terraform init
                terraform plan -out=tfplan
                terraform show -json tfplan > tfplan.json
                opa eval --format pretty --input tfplan.json --data ../policies/policy.rego "data.terraform.deny"
                '''
            }
        }

        stage('Terraform Apply') {
            steps {
                input message: 'Proceed with Terraform apply?'
                sh 'terraform apply -auto-approve'
            }
        }
    }
}
