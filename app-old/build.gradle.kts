plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.8.21-1.0.11"
}

android {
    namespace = "com.heyanle.closure"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.heyanle.closure"
        minSdk = 24
        targetSdk = 34
        versionCode = 11
        versionName = "2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),  "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-Xjvm-default=all"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    aaptOptions {
        noCompress += "json"
    }
}

//repositories {
//    maven {url = uri("https://jitpack.io")}
//    google()
//    mavenCentral()
//}

dependencies {


}