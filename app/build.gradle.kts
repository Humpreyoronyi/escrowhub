plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.escrowhub"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.escrowhub"
        minSdk = 24
        targetSdk = 36
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Firebase deependencies
    // Firebase BOM - management dependency for firebase products
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    // firebase authentication product(auth)
    implementation("com.google.firebase:firebase-auth-ktx")
    // database
    implementation("com.google.firebase:firebase-firestore-ktx")
    //storage
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.play.services.wallet)
    // Coroutines support for firebase i.e. a way of handling background process
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")


    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    // navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    // coil : image routing
    implementation("io.coil-kt:coil-compose:2.6.0")
    // viewmodel : (data management and compose rendering)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    // Get icons from material design
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation(libs.firebase.crashlytics.buildtools)


    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}