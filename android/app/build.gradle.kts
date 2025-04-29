import java.util.Properties

    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
        id("kotlin-parcelize")
    }

    val apikeyProperties = Properties().apply {
        load(rootProject.file("apikey.properties").inputStream())
    }

    android {
        namespace = "com.example.pickandcook"
        compileSdk = 34

        defaultConfig {
            applicationId = "com.example.pickandcook"
            minSdk = 24
            targetSdk = 34
            versionCode = 1
            versionName = "1.0"
            buildConfigField(
                "String",
                "API_KEY",
                "\"${apikeyProperties.getProperty("API_KEY")}\""
            )
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



        buildFeatures {
            dataBinding = true
            viewBinding = true
            buildConfig = true
        }

    }

    dependencies {
        //초기 라이브러리
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)


        // Retrofit 및 Gson(json 변환 라이브러리) 의존성 추가
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

        //ZXing 라이브러리 추가 (바코드 리더기)
        implementation("com.journeyapps:zxing-android-embedded:4.3.0")
        implementation("com.google.zxing:core:3.4.1")

        //태경 코드
        //implementation(libs.litert.support.api)
        implementation("com.google.android.flexbox:flexbox:3.0.0")
        implementation ("androidx.fragment:fragment-ktx:1.6.2")
        implementation("com.google.code.gson:gson:2.10.1")
        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
        implementation ("mysql:mysql-connector-java:5.1.49")
        implementation("org.jsoup:jsoup:1.15.3")
        implementation("androidx.core:core-ktx:1.12.0")
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")


        implementation("com.github.bumptech.glide:glide:4.16.0")
        annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    }