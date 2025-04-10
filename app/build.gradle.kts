plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("org.cyclonedx.bom")
}

android {
    namespace = "com.harman.vehicleconnects"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.harman.vehicleconnects"
        minSdk = 24
        targetSdk = 34
        versionCode = 13
        versionName = "2.5"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
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
        group = "com.harman.vehicleconnects"
        version = 1.0
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        abortOnError = false
    }
}

dependencies {

    implementation(files("libs/androidVehicleConnectSDK.aar"))

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.ui:ui-graphics:1.6.8")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.8")
    implementation("androidx.compose.material3:material3:1.3.0-alpha02")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation("org.mockito:mockito-inline:2.13.0")
    testImplementation("org.powermock:powermock-module-junit4:2.0.0-beta.5")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.0-beta.5")
    testImplementation("androidx.test:rules:1.6.1")
    testImplementation("androidx.test:runner:1.6.1")
    testImplementation("org.robolectric:robolectric:4.11.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.8")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.openid:appauth:0.11.1")
    implementation("androidx.multidex:multidex:2.0.1")

    implementation("com.google.dagger:dagger:2.50")
    implementation("com.google.dagger:dagger-android:2.50")
    implementation("com.google.dagger:dagger-android-support:2.50")
    kapt("com.google.dagger:dagger-android-processor:2.50")
    kapt("com.google.dagger:dagger-compiler:2.50")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation(kotlin("reflect"))

    /*// Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    // When using the BoM, don't specify versions in Firebase dependenci
    es
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")*/

    implementation ("com.google.firebase:firebase-messaging:24.0.0")
    implementation ("com.google.firebase:firebase-core:21.1.1")

}