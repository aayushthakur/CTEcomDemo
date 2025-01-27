plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.clevertap.demo.ecom"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.clevertap.demo.ecom"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.core.splashscreen)
//    implementation(libs.androidx.room.compiler)
//    implementation(libs.ksp)
//    kapt(libs.compiler.v4151)
    implementation (libs.push.templates)
    implementation (libs.clevertap.android.sdk)
//    implementation (libs.glide)
    implementation(libs.gson)
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.0")
    implementation(libs.picasso)
    implementation("com.google.firebase:firebase-messaging:24.0.0")

    //MANDATORY for App Inbox

    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager)
    implementation("com.github.bumptech.glide:glide:4.12.0")

//Optional AndroidX Media3 Libraries for Audio/Video Inbox Messages. Audio/Video messages will be dropped without these dependencies

    implementation (libs.androidx.media3.exoplayer)
    implementation (libs.media3.exoplayer.hls)
    implementation (libs.media3.ui)

}