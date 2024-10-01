

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.text_to_speech_3"
    compileSdk = 34

    packagingOptions{
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.excludes.add("META-INF/DEPENDENCIES")
    }

    defaultConfig {
        applicationId = "com.example.text_to_speech_3"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.runtime.android)
//    implementation(libs.androidx.games.activity)
//    implementation(libs.androidx.games.text.input)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.cloud:google-cloud-texttospeech:2.5.0")
    implementation("com.google.cloud:google-cloud-speech:2.5.0")
    implementation("com.google.protobuf:protobuf-java:3.15.6")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation(kotlin("script-runtime"))
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")


    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    implementation("com.android.volley:volley:1.2.1")

    implementation("com.google.firebase:firebase-ml-vision:24.0.3")

    implementation ("androidx.sqlite:sqlite-ktx:2.1.0")

    implementation("androidx.fragment:fragment-ktx:1.8.3")
    implementation("androidx.activity:activity-ktx:1.9.2")
}