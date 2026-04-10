plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
//    alias(libs.plugins.legacy.kapt)
}

android {
    namespace = "com.example.podcastapp.core.data"
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
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    api(project(":core:audio-processing"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.paging.runtime)
    implementation(libs.androidx.core.ktx)

    implementation(libs.hilt.android)
    implementation(libs.hilt.work)
    implementation(libs.work.runtime.ktx)
    ksp(libs.hilt.compiler)
//    kapt(libs.hilt.compiler)
    ksp(libs.hilt.androidx.compiler)
}
