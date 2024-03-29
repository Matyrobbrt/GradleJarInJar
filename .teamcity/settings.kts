import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.Swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.add

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

version = "2022.10"

project {
    buildType(Matyrobbrt_GradleJarInJar_Build)
}

object Matyrobbrt_GradleJarInJar_Build : BuildType({
    id("Build")
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    triggers {
        vcs {
            triggerRules = "-:comment=\\[noci]:**"
        }
    }

    features {
        add {
            swabra {
                filesCleanup = Swabra.FilesCleanup.AFTER_BUILD
                lockingProcesses = Swabra.LockingProcessPolicy.KILL
            }
        }
        add {
            commitStatusPublisher {
                publisher = github {
                    githubUrl = "https://api.github.com"
                    authType = personalToken {
                        token = "%commit_status_publisher%"
                    }
                }
            }
        }
    }

    steps {
        gradle {
            name = "Configure TeamCity information"
            tasks = "configureTeamCity"
        }

        gradle {
            name = "Build Gradle Project"
            tasks = "clean publishPlugins"
        }
    }
})