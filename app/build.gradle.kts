plugins {
    alias(build.plugins.android.application)
    alias(build.plugins.kotlin.android)
    alias(build.plugins.ksp)
}

android {
    namespace = "com.heyanle.closure"
    compileSdk = 34

    defaultConfig {
        targetSdk = 34
        applicationId = "com.heyanle.closure"
        minSdk = 21
        versionCode = 12
        versionName = "3.0"

        buildConfigField(
            "String",
            "APP_CENTER_SECRET",
            "\"${
                System.getenv("APPCENTER_SECRET") ?: ""
            }\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    kotlin {
        jvmToolchain(17)
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + "-Xjvm-default=all"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = build.versions.compose.compiler.get()
    }
}


dependencies {
    implementation(androidx.bundles.core)

    implementation(androidx.preference.ktx)

    implementation(androidx.google.material)

    implementation(androidx.paging.common)
    implementation(androidx.paging.compose)
    implementation(androidx.paging.runtime.ktx)

    implementation(compose.bundles.ui)
    implementation(compose.bundles.runtime)
    implementation(compose.bundles.animation)
    implementation(compose.bundles.foundation)
    implementation(compose.bundles.material)
    implementation(compose.bundles.material3)

    debugImplementation(compose.ui.tooling)

    implementation(libs.bundles.okhttp3)
    implementation(libs.bundles.appcenter)

    implementation(libs.moshi)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.accompanist.permissions)
    implementation(libs.navigtion.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    implementation(libs.compose.reorderable)

//    implementation(libs.koin.core)
//    implementation(libs.koin.android)

    implementation(libs.ktor.json)
    implementation(libs.ktor.core)
    implementation(libs.ktor.android)
    implementation(libs.ktor.moshi)
    implementation(libs.ktor.negotiation)
    implementation(libs.ktor.negotiation)

    implementation(libs.okkv2)

    implementation(libs.geetest.sensebot)

    implementation(project(":crasher"))
    implementation(project(":i18n"))
    implementation(project(":injekt"))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
}