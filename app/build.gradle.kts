plugins {
    alias(libs.plugins.android.application)
}

fun String.asBuildConfigString(): String =
    "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""

val configuredApiBaseUrl = providers.gradleProperty("FCM_API_BASE_URL")
    .orElse(providers.environmentVariable("FCM_API_BASE_URL"))
    .getOrElse("http://10.0.2.2:3000/")
val configuredApiKey = providers.gradleProperty("FCM_API_KEY")
    .orElse(providers.environmentVariable("FCM_API_KEY"))
    .getOrElse("")

android {
    namespace = "com.eaut.footballclubmanagement"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.eaut.footballclubmanagement"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_BASE_URL", configuredApiBaseUrl.asBuildConfigString())
        buildConfigField("String", "API_KEY", configuredApiKey.asBuildConfigString())
    }

    buildTypes {
        debug {
            manifestPlaceholders["usesCleartextTraffic"] = "true"
        }
        release {
            manifestPlaceholders["usesCleartextTraffic"] = "false"
            optimization {
                enable = false
            }
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    
    // Thư viện gọi API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Thư viện Room Database (Offline Caching)
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    // Hiệu ứng Loading xịn (Shimmer)
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    
    // Thư viện WorkManager cho Background Tasks (Lập lịch thông báo)
    val work_version = "2.8.1"
    implementation("androidx.work:work-runtime:$work_version")
    
    // Kiến trúc MVVM (ViewModel & LiveData)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2")
    
    // Biểu đồ Radar Chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
