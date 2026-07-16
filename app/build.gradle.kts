plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Ktlint plugin for code formatting and linting
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "com.monarch.pos"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.monarch.pos"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "STRIPE_PUBLISHABLE_KEY",
            "\"${System.getenv("STRIPE_PUBLISHABLE_KEY") ?: ""}\"")
        buildConfigField("String", "BACKEND_URL",
            "\"${System.getenv("BACKEND_URL") ?: "https://monarch-pinpad.monarchdetailing.workers.dev"}\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // Material Components (required for Theme.MaterialComponents in themes.xml)
    implementation("com.google.android.material:material:1.9.0")

    // Stripe Terminal SDK (Apps on Devices)
    implementation("com.stripe:stripeterminal-core:5.6.0")
    implementation("com.stripe:stripeterminal-appsondevices:5.6.0")

    // HTTP for BackendClient
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Navigation - Compose (required for NavHost, composable, rememberNavController)
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
}
