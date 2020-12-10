import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.1"

project {
    description = "test description"

    vcsRoot(HttpsGithubComG0t4teamcityCourseCards)

    buildType(id03DeployToStaging)
    buildType(id02Chrome)
    buildType(id01FastTests)
    buildType(id03Firefox)

    template(Template_1)
}

object id01FastTests : BuildType({
    templates(Template_1)
    id("01FastTests")
    name = "01. Fast Tests"

    params {
        param("Browser", "PhantomJS")
    }

    steps {
        script {
            name = "Restore NPM packages"
            id = "RUNNER_6"
            scriptContent = "npm install"
        }
    }
})

object id02Chrome : BuildType({
    templates(Template_1)
    id("02Chrome")
    name = "02. Chrome"

    params {
        param("Browser", "Chrome")
    }

    dependencies {
        snapshot(id01FastTests) {
        }
    }
})

object id03DeployToStaging : BuildType({
    id("03DeployToStaging")
    name = "03. Deploy To Staging"

    vcs {
        root(HttpsGithubComG0t4teamcityCourseCards)
    }

    dependencies {
        snapshot(id02Chrome) {
        }
        snapshot(id03Firefox) {
        }
    }
})

object id03Firefox : BuildType({
    templates(Template_1)
    id("03Firefox")
    name = "02. Firefox"

    params {
        param("Browser", "Firefox")
    }

    dependencies {
        snapshot(id01FastTests) {
        }
    }
})

object Template_1 : Template({
    id("Template")
    name = "Template"

    vcs {
        root(HttpsGithubComG0t4teamcityCourseCards)
    }

    steps {
        script {
            name = "Restore NPM packages"
            id = "RUNNER_6"
            scriptContent = "npm install"
        }
        script {
            name = "Browser test"
            id = "RUNNER_7"
            scriptContent = "npm test -- --single-run --browsers %Browser% --colors false --reporters teamcity"
        }
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            perCheckinTriggering = true
            enableQueueOptimization = false
        }
    }
})

object HttpsGithubComG0t4teamcityCourseCards : GitVcsRoot({
    name = "https://github.com/g0t4/teamcity-course-cards"
    url = "https://github.com/g0t4/teamcity-course-cards"
})
