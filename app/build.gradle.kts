plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ta_avance"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ta_avance"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
    }

    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled  = true
    }
    kotlin{
        jvmToolchain(17)
    }
}

dependencies {

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.auth0.android:jwtdecode:2.0.1")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.google.android.material:material:1.12.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")

    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
}