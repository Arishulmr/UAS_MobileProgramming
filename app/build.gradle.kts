plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.khalil.myfoods_mohammadkhalilardhani"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.khalil.myfoods_mohammadkhalilardhani"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.volley)
    implementation (libs.lottie)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.recyclerview.v121)
implementation (libs.glide)
    annotationProcessor (libs.compiler)
}