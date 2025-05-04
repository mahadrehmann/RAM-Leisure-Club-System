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

    implementation ("androidx.recyclerview:recyclerview:1.2.1")

    implementation (files("libs/exifinterface-1.3.7.aar"))
    implementation(files("libs/camera-camera2-1.4.1.aar"))
    implementation(files("libs/camera-lifecycle-1.4.1.aar"))
    implementation(files("libs/camera-view-1.4.1.aar"))
    implementation(libs.androidx.core.ktx)
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}