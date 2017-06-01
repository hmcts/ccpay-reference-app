#!groovy

@Library('Reform') _

properties(
    [[$class: 'GithubProjectProperty', displayName: 'Reference App', projectUrlStr: 'https://git.reform.hmcts.net/common-components/reference-app'],
    pipelineTriggers([[$class: 'GitHubPushTrigger']])]
)

stageWithNotification('Checkout') {
    checkout scm
}

stageWithNotification('Build (JAR)') {
    def rtMaven = Artifactory.newMavenBuild()
    rtMaven.tool = 'apache-maven-3.3.9'
    rtMaven.run pom: 'pom.xml', goals: 'clean install'
    archiveArtifacts 'api/target/*.jar'
}

stageWithNotification('Build (Docker)') {
    dockerImage imageName: 'common-components/reference-api'
}

private stageWithNotification(String name, Closure body) {
    stage(name) {
        node {
            try {
                body()
            } catch (err) {
                notifyBuildFailure channel: '#cc_tech'
                throw err
            }
        }
    }
}
