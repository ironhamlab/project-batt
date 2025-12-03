import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.a506.batticket"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.a506.batticket"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "NATIVE_APP_KEY", "\"${getLocalProperty("NATIVE_APP_KEY")}\"")
        manifestPlaceholders["NATIVE_APP_KEY"] = getLocalProperty("NATIVE_APP_KEY")
        buildConfigField("String", "NAVER_CLIENT_ID", "\"${getLocalProperty("NAVER_CLIENT_ID")}\"")
        buildConfigField("String", "NAVER_CLIENT_SECRET_KEY", "\"${getLocalProperty("NAVER_CLIENT_SECRET_KEY")}\"")
        buildConfigField("String", "TOSS_CERT_ACCESS_TOKEN", "\"${getLocalProperty("TOSS_CERT_ACCESS_TOKEN")}\"")

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
        viewBinding = true
        buildConfig = true
    }
}

fun getLocalProperty(key: String) :String {
    val properties = Properties()
    properties.load(File(rootDir, "local.properties").inputStream())
    return properties.getProperty(key)
}

dependencies {
    // 로그인 SDK는 나중에 실제 구현 시 추가 예정
     implementation(libs.v2.user)
     implementation(libs.oauth)

    // QR 코드 라이브러리
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit
    implementation(libs.retrofit)
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
// OkHttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation("androidx.datastore:datastore-preferences:1.1.7")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2")
}
