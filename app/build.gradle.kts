plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.kakos.uniAID"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kakos.uniAID"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.kakos.uniAID.HiltTestRunner"
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
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {

    /**
     * Core
     */

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)


    /**
     * TESTS and DEBUG
     */

    // Local Tests
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.mockk)
    testImplementation(libs.junit.junit)
    debugImplementation(libs.ui.test.manifest)

    // Instrumented Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.core.ktx)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.mockwebserver)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.dagger.hilt.android.compiler)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    /**
     * COMPOSE
     */

    // The compose calendar library for Android
    implementation(libs.compose)
    // ViewModel utilities for Compose
    implementation(libs.lifecycle.viewmodel.compose)
    // Navigation Compose
    implementation(libs.androidx.navigation.compose)


    /**
     * NAVIGATION
     */

    // Gson
    implementation(libs.gson)
    // Serialization
    implementation(libs.kotlinx.serialization.json)

    /**
     * ROOM
     */
    // Room - Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.compose.material.core)

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    ksp(libs.room.compiler)

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    /**
     * DEPENDENCY INJECTION
     */

    // Dagger-Hilt - Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    /**
     * DataStore
     */

    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)

}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}