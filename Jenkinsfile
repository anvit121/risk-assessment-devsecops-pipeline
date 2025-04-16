pipeline {
    agent any

/*    tools {
        maven 'maven'
    }

    environment {
        SONARQUBE_SCANNER = tool 'SonarQubeScanner'
    }
*/
    stages {
        stage('Checkout') {
            steps {
                echo 'Code already checked out by Jenkins'
            }
        }

/*        stage('Build Java') {
            steps {
                dir('src/main/java') {
                    sh 'mvn clean install'
                }
            }
        }

        stage('SonarQube Scan') {
            steps {
                withSonarQubeEnv('SonarQubeScanner') {
                    sh 'sonar-scanner'
                }
            }
        }
*/

        stage('Snyk Scan') {
            environment {
                SNYK_TOKEN = credentials('SNYK_TOKEN') // Automatically pulled from Jenkins secret store
            }
            steps {
                dir('src/main') {
                    sh '''
                    npm install || true
                    snyk test --json-file-output=../../snyk_results.json || echo "Snyk scan found vulnerabilities, but continuing..." || true
                    '''
                }
            }
        }


        stage('Bandit Python Scan') {
            steps {
                dir('src/main/python') {
                    sh 'bandit -r . -f json -o ../../bandit_results.json'
                }
            }
        }

        stage('Terraform Plan & OPA Policy') {
            steps {
                dir('src/main/terraform') {
                    sh '''
                    set -e
                    echo "[INFO] Initializing Terraform"
                    terraform init
                    echo "[INFO] Running terraform plan"
                    terraform plan -out=tfplan
                    echo "[INFO] Converting terraform plan to JSON"
                    terraform show -json tfplan > tfplan.json
                    echo "[INFO] Evaluating OPA policy"
                    opa eval --format json --input tfplan.json --data ../../../policies/policy.rego "data.terraform.deny" > ../../../opa_results.json
                    '''
                }
            }
        }


        stage('Send Results to Elasticsearch') {
            steps {
                sh '''
                curl -X POST http://localhost:9200/devsecops/_doc -H "Content-Type: application/json" -d @bandit_results.json || true
                curl -X POST http://localhost:9200/devsecops/_doc -H "Content-Type: application/json" -d @snyk_results.json || true
                curl -X POST http://localhost:9200/devsecops/_doc -H "Content-Type: application/json" -d @opa_results.json || true
                '''
            }
        }

        stage('Terraform Apply') {
            steps {
                input message: 'Proceed with Terraform apply?'
                dir('src/main/terraform') {
                    sh 'terraform apply -auto-approve'
                }
            }
        }
    }
}
