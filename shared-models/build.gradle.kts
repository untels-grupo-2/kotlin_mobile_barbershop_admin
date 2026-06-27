plugins {
    id("com.android.library") version "8.9.2"
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.shared.models"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation("com.google.code.gson:gson:2.10.1")
}
