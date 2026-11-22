pipeline {
    agent any

    tools {
        maven 'MAVEN-3.9.19'
        jdk 'JDK-17'
    }

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Browser to run on')
        choice(name: 'SUITE', choices: ['testng.xml', 'smoke.xml'], description: 'Which suite')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run without a visible browser')
    }

    triggers {
        cron('H 2 * * *')
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
                runCmd('mvn -B clean test-compile')
            }
        }

        stage('Test') {
            steps {
                runCmd("mvn -B test -Dsuite=${params.SUITE} -Dbrowser=${params.BROWSER} -Dheadless=${params.HEADLESS}")
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
        }
    }
}

// Runs a command with sh on Linux/Mac agents and bat on Windows agents.
def runCmd(String cmd) {
    if (isUnix()) {
        sh cmd
    } else {
        bat cmd
    }
}