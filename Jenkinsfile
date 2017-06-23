#!groovy
@Library("Reform")
import uk.gov.hmcts.Packager

def packager = new Packager(this, 'cc')

properties(
    [[$class: 'GithubProjectProperty', displayName: 'Reference App', projectUrlStr: 'https://git.reform.hmcts.net/common-components/reference-app'],
     pipelineTriggers([[$class: 'GitHubPushTrigger']])]
)

milestone()
lock(resource: "reference-app-${env.BRANCH_NAME}", inversePrecedence: true) {
    node {
        try {
            stage('Checkout') {
                deleteDir()
                checkout scm
            }

            stage('Build (JAR)') {
                def rtMaven = Artifactory.newMavenBuild()
                rtMaven.tool = 'apache-maven-3.3.9'
                rtMaven.run pom: 'pom.xml', goals: 'clean install'
            }

            ifMaster {
                def rpmVersion

                stage("Publish RPM") {
                    rpmVersion = packager.javaRPM('master', 'reference-api', '$(ls api/target/reference-api-*.jar)', 'springboot', 'api/src/main/resources/application.properties')
                    packager.publishJavaRPM('reference-api')
                }

                stage("Trigger acceptance tests") {
                    build job: '/common-components/reference-web-acceptance-tests/master', parameters: [
                        [$class: 'StringParameterValue', name: 'referenceWebRpmVersion', value: '-1'],
                        [$class: 'StringParameterValue', name: 'referenceApiRpmVersion', value: rpmVersion]
                    ]
                }
            }

            milestone()
        } catch (err) {
            notifyBuildFailure channel: '#cc_tech'
            throw err
        }
    }
}

private ifMaster(Closure body) {
    if ("master" == "${env.BRANCH_NAME}") {
        body()
    }
}
