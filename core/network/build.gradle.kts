plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.podcastapp.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = false
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(libs.okhttp)
    implementation(libs.kotlinx.coroutines.android)
}
