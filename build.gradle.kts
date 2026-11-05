import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    // 宿主工程（Sandbox）用 AGP 9 的内置 Kotlin：KGP 随 AGP 一起上 classpath 并已注册 kotlin 扩展，
    // 这里只登记插件版本、不应用，否则会报 "Cannot add extension with name 'kotlin'"。
    alias(libs.plugins.kotlin.android) apply false
}

// 独立构建（AGP 8.x，没有内置 Kotlin）时才自己应用 KGP。
if (extensions.findByName("kotlin") == null) {
    apply(plugin = "org.jetbrains.kotlin.android")
}

android {
    namespace = "com.bhh.base"
    // 与宿主 App 保持一致：本树 SDK 是 API 36.1（AGP 8.13.2 / 9.3.3 都支持 compileSdkMinor）
    compileSdk = 36
    compileSdkMinor = 1

    defaultConfig {
        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// 不用 `kotlin { }` 类型安全访问器：宿主工程里 KGP 是由 AGP 内置 Kotlin 注册的，
// 只有独立构建才由脚本主动 apply，访问器不会生成。按类型取扩展两种场景都能用。
extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension>("kotlin") {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // com.bhh.tools.*（ApplicationHandler / GlobalScheduler / EventBus / Logger）
    // 由宿主工程（deviceMaster）提供，这里仅编译期可见，不打进产物。
    // 独立构建（单仓库打开）时该子项目不存在，跳过以便配置阶段可正常通过。
    if (rootProject.findProject(":tools") != null) {
        compileOnly(project(":tools"))
    }

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
