pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://repository.map.naver.com/archive/maven")
    }
}

rootProject.name = "ttatta"
include(":app")
include(":core")
include(":feature:login")
include(":feature:challenge")
include(":feature:footprint")
include(":feature:mypage")
include(":feature:home")
include(":feature:record")
include(":data")
include(":design")
include(":feature:category")
