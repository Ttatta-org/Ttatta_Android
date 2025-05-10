import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Hilt
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)

    // FCM
    id("com.google.gms.google-services")
}

val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.umc.ttatta"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.umc.ttatta"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        listOf(
            "KAKAO_NATIVE_APP_KEY",
            "NAVER_SDK_CLIENT_ID"
        ).forEach {
            buildConfigField("String", it, "\"${localProperties.getProperty(it)}\"")
        }

        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = localProperties.getProperty("KAKAO_NATIVE_APP_KEY")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.messaging.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // 의존성 정의
    implementation(project(":feature:login"))
    implementation(project(":feature:challenge"))
    implementation(project(":feature:footprint"))
    implementation(project(":feature:mypage"))
    implementation(project(":feature:home"))
    implementation(project(":feature:record"))
    implementation(project(":feature:category"))
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":design"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.complier)
    implementation(libs.androidx.hilt.navigation)
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // 네이버 지도 SDK
    implementation(libs.naver.map)

    // 카카오 로그인
    implementation("com.kakao.sdk:v2-user:2.20.6")

    // FCM
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")

}