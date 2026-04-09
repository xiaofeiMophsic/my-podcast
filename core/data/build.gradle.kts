plugins {
    alias(libs.plugins.android.library)
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
    implementation(project(":core:media"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.paging.runtime)
    implementation(libs.androidx.core.ktx)

    implementation(libs.hilt.work)
    implementation(libs.work.runtime.ktx)
}
