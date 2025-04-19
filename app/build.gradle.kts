plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.food_runs"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.food_runs"
        minSdk = 23
        targetSdk = 35
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase BoM to align all Firebase versions
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))

    // Firebase dependencies (no version needed)
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Optional Firebase tools
    // implementation("com.google.firebase:firebase-analytics")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.recyclerview)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Lottie animation
    implementation("com.airbnb.android:lottie:6.1.0")

    // OkHttp for network requests
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.razorpay:checkout:1.6.40")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

