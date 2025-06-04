import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
}
val localProperties = Properties().apply {
    // Проверяем, существует ли файл local.properties в корне проекта
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        // Если да, читаем его с помощью reader() и загружаем свойства
        localFile.reader().use { load(it) }
    }
}

// Достаём конкретное свойство (или пустую строку, если нет)
val TMDB_API_KEY: String = localProperties.getProperty("TMDB_API_KEY", "")
android {
    namespace = "com.example.platonov"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.platonov"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "TMDB_API_KEY", "\"${TMDB_API_KEY}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true     // включаем ViewBinding, чтобы не писать findViewById
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.viewmodel.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Jetpack Navigation (для Single Activity + Fragments)
    implementation("androidx.navigation:navigation-fragment:2.7.2")
    implementation("androidx.navigation:navigation-ui:2.7.2")

    // Room (локальная база)
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    // Если захотите использовать Kotlin-синтетические методы (wrapper-классы) — не обязательно для Java
    //implementation("androidx.room:room-ktx:2.6.1")

    // Retrofit + Gson (для сетевого API)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Для логов HTTP (опционально)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Lifecycle / ViewModel (чтобы ViewModel уже была готова)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.2")
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.2")

    // Корутине-поддержка (необязательно, если пишем на Java, но можно убрать ниже строку, если не планируете Kotlin Coroutines)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Glide или Picasso (для загрузки постеров)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.10")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.2")
    implementation("androidx.navigation:navigation-fragment:2.7.2")
    implementation("androidx.navigation:navigation-ui:2.7.2")
}