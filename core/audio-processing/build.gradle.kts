plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.podcastapp.core.audioprocessing"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.amplituda)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core.ktx)
}
