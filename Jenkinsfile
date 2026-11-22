// Declarative pipeline. In Jenkins: New Item > Pipeline > "Pipeline script from SCM" > point to this repo.
// Needs plugins: Pipeline, Git, JUnit. Configure tools named 'Maven3' and 'JDK17' in Manage Jenkins > Tools.
pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Browser to run on')
        choice(name: 'SUITE', choices: ['testng.xml', 'smoke.xml'], description: 'Which suite')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run without a visible browser')
    }

    triggers {
        cron('H 2 * * *')        // nightly around 2 AM (server time)
        // pollSCM('H/5 * * * *') // or: check Git for changes every 5 minutes
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean test-compile'
            }
        }

        stage('Test') {
            steps {
                sh "mvn -B test -Dsuite=${params.SUITE} -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}"

                // Real projects: keep passwords in Manage Jenkins > Credentials ("Secret text", ID app-password)
                // and replace the sh line above with:
                // withCredentials([string(credentialsId: 'app-password', variable: 'PASSWORD')]) {
                //     sh "mvn -B test -Dsuite=${params.SUITE} -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}"
                // }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
        }
        success {
            echo 'All tests passed'
        }
        failure {
            echo 'Tests failed - check screenshots in build artifacts'
            // mail to: 'team@example.com', subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}", body: "${env.BUILD_URL}"
        }
    }
}
