pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven { url = uri("https://api.xposed.info/") }
        maven("https://jitpack.io")
    }
}

rootProject.name = "BaseModule"

// 独立仓库构建时，自动挂载同级目录下的 tools 子模块（com.bhh.tools.* 的编译期来源）。
// 被 deviceMaster 作为子模块引用时，下面的 include 会被宿主 settings 覆盖前跳过：
// 此时 rootProject 不是 BaseModule，交由宿主工程统一管理 include。
if (rootProject.name == "BaseModule") {
    val toolsDir = file("../tools")
    if (toolsDir.resolve("build.gradle.kts").exists()) {
        include(":tools")
        project(":tools").projectDir = toolsDir
    }
}
