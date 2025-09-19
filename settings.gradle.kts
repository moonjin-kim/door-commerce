rootProject.name = "door-commerce"

include(
    ":apps:commerce-streamer",
    ":apps:commerce-api",
    ":apps:pg-simulator",
    ":apps:commerce-batch",
    ":modules:jpa",
    ":modules:redis",
    ":modules:kafka",
    ":supports:jackson",
    ":supports:logging",
    ":supports:monitoring",
)

// configurations
pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings

    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.springframework.boot" -> useVersion(springBootVersion)
                "io.spring.dependency-management" -> useVersion(springDependencyManagementVersion)
            }
        }
    }
}
include("modules:kafka")
findProject(":modules:kafka")?.name = "kafka"
include("apps:commerce-batch")
findProject(":apps:commerce-batch")?.name = "commerce-batch"
include("apps:commerce-batch")
findProject(":apps:commerce-batch")?.name = "commerce-batch"
