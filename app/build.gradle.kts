plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.arshman.mahad.rehan"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.arshman.mahad.rehan"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Set FCM default notification channel ID
        manifestPlaceholders["onesignal_app_id"] = "e31abef6-4c88-4338-8d3e-d4fb59f34de1"
        manifestPlaceholders["onesignal_google_project_number"] = "REMOTE"
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
}

dependencies {
    // Retrofit + Gson

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

// Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
// Glide for image loading
    implementation ("com.github.bumptech.glide:glide:4.12.0")
//    kapt ("com.github.bumptech.glide:compiler:4.12.0")



    implementation("androidx.recyclerview:recyclerview:1.3.2")

    implementation("androidx.exifinterface:exifinterface:1.3.7")

//    implementation(files("libs/exifinterface-1.3.7.aar"))
    implementation(files("libs/camera-camera2-1.4.1.aar"))
    implementation(files("libs/camera-lifecycle-1.4.1.aar"))
    implementation(files("libs/camera-view-1.4.1.aar"))
    implementation(libs.androidx.core.ktx)
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase dependencies
//    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.messaging) // FCM dependency
    implementation(libs.firebase.dataconnect)
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-messaging-ktx")

    // OneSignal SDK - using the latest version
    implementation("com.onesignal:OneSignal:4.8.6")
    // OkHttp for network requests
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}