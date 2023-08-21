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
        versionCode = 10
        versionName = "2.0"

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

    implementation ("androidx.core:core-ktx:1.10.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation ("androidx.activity:activity-compose:1.7.2")

    val compose_animation = "1.5.0-rc01"
    val compose_compiler = "1.4.3"
    val compose_foundation = "1.5.0-rc01"
    val compose_material = "1.5.0-rc01"
    val compose_material3 = "1.2.0-alpha04"
    val compose_runtime = "1.5.0-rc01"
    val compose_ui = "1.5.0-rc01"

    implementation("androidx.compose.ui:ui:${compose_ui}")
    implementation("androidx.compose.ui:ui-graphics:${compose_ui}")
    implementation("androidx.compose.ui:ui-tooling-preview:${compose_ui}")
    implementation("androidx.compose.material3:material3:${compose_material3}")
    implementation("androidx.compose.material:material-icons-core:${compose_material}")
    implementation("androidx.compose.material:material-icons-extended:${compose_material}")
    implementation("androidx.compose.runtime:runtime-livedata:${compose_runtime}")
    implementation("androidx.compose.animation:animation:${compose_animation}")
    implementation("androidx.compose.animation:animation-core:${compose_animation}")
    implementation("androidx.compose.animation:animation-graphics:${compose_animation}")
    implementation("androidx.compose.foundation:foundation:${compose_foundation}")
    implementation("androidx.compose.foundation:foundation-layout:${compose_foundation}")

    implementation("androidx.compose.material3:material3-window-size-class:${compose_material3}")

    debugImplementation("androidx.compose.ui:ui-tooling:${compose_ui}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${compose_ui}")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:${compose_ui}")



    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.preference:preference-ktx:1.2.1")

    implementation("com.google.android.material:material:1.9.0")

    implementation ("androidx.navigation:navigation-compose:2.7.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.google.code.gson:gson:2.10")
    implementation ("com.squareup.retrofit2:converter-gson:2.4.0")
    implementation ("com.github.heyanLE.okkv2:okkv2-mmkv:1.3.5")
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    implementation ("com.github.bumptech.glide:compose:1.0.0-alpha.1")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    val accompanistVersion = "0.33.0-alpha"
//    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
//    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
//    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")
//    implementation("com.google.accompanist:accompanist-swiperefresh:$accompanistVersion")
//    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
//    implementation("com.google.accompanist:accompanist-insets-ui:$accompanistVersion")
//    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")

    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil-gif:2.4.0")

    implementation("com.geetest.sensebot:sensebot:4.3.8.1")

    val appCenterSdkVersion = "5.0.2"
    implementation ("com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}")
    implementation ("com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}")
    implementation("com.microsoft.appcenter:appcenter-distribute:$appCenterSdkVersion")



    implementation("com.squareup.moshi:moshi:1.14.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")

    implementation(project(":injekt"))
}