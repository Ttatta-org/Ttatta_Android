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
include(":feature:category")
include(":feature:challenge")
include(":feature:footprint")
include(":feature:home")
include(":feature:login")
include(":feature:mypage")
include(":feature:record")
include(":data")
include(":design")
include(":test:category")
include(":test:challenge")
include(":test:footprint")
include(":test:home")
include(":test:login")
include(":test:mypage")
include(":test:record")
