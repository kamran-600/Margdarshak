plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.margdarshakendra.margdarshak"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.margdarshakendra.margdarshak"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
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


    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("com.google.firebase:firebase-messaging:23.4.0")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // country code picker
    implementation("com.hbb20:ccp:2.7.3")

    // hilt dependency
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")


    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // lifecycle of viewmodel and livedata
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    //ViewModels delegation extensions for activity
    implementation("androidx.activity:activity-ktx:1.8.1")

    implementation("androidx.fragment:fragment-ktx:1.6.2")


    // auto read otp library
    implementation("com.google.android.gms:play-services-auth-api-phone:18.0.1")

    // circular image
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // lottie animation
    implementation("com.airbnb.android:lottie:6.1.0")

    // glide library
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // skeleton for shimmer effect
    implementation("com.faltenreich:skeletonlayout:5.0.0")

    //sweetDialog library
    implementation("com.github.f0ris.sweetalert:library:1.6.2")

    //flowLayout
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    //loading Library
    implementation("com.github.ybq:Android-SpinKit:1.4.0")

    // pdf viewer
    implementation("com.github.afreakyelf:Pdf-Viewer:2.0.4")

    // charts
    /// implementation("com.github.highcharts:highcharts-android:11.2.0")
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
    //  implementation("com.github.AnyChart:AnyChart-Android:1.1.5")
    //implementation("com.github.AAChartModel:AAChartCore-Kotlin")

    implementation("ru.noties:jlatexmath-android:0.2.0")
    // for Cyrillic symbols
    implementation("ru.noties:jlatexmath-android-font-cyrillic:0.2.0")
    // for Greek symbols
    implementation("ru.noties:jlatexmath-android-font-greek:0.2.0")

    implementation("com.applandeo:material-calendar-view:1.9.0-rc04")

    implementation("com.kizitonwose.calendar:view:2.4.1")



}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}
