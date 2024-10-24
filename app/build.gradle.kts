plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx) // Añade otras dependencias
    implementation(libs.androidx.appcompat)
    implementation(platform("com.google.firebase:firebase-bom:33.5.0")) // Esta línea es importante
    implementation("com.google.firebase:firebase-auth") // Reemplaza con las dependencias que necesitas
    implementation("com.google.firebase:firebase-firestore") // Agrega Firestore si es necesario
    implementation ("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-database-ktx")
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.junit.ktx)

    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation(libs.play.services.maps)
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.auth.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}