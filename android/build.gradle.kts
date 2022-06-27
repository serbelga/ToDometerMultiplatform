plugins {
    alias(libs.plugins.composeMultiplatform)
    id("com.android.application")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("android")
    kotlin("kapt")
}

// TODO: Workaround until https://issuetracker.google.com/issues/223240936 is fixed
androidComponents {
    onVariants(selector().all()) {
        val capitalizedVariantName = it.name.substring(0, 1).toUpperCase() + it.name.substring(1)
        afterEvaluate {
            tasks.named("map${capitalizedVariantName}SourceSetPaths").configure {
                dependsOn("process${capitalizedVariantName}GoogleServices")
            }
        }
    }
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.sergiobelda.todometer"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 1220102
        versionName = "android-2.2.0-alpha02"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            firebaseCrashlytics {
                mappingFileUploadEnabled = false
            }
        }
        lint {
            abortOnError = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
    namespace = "dev.sergiobelda.todometer"
}

dependencies {
    implementation(projects.common)
    implementation(projects.commonAndroidResources)
    implementation(projects.commonComposeUi)

    implementation(libs.androidx.activityCompose)
    implementation(libs.androidx.compose.animation.graphics)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.androidx.splashscreen)

    implementation(libs.material)

    implementation(libs.timber)

    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.core)

    implementation(libs.accompanist.systemUiController)

    implementation(libs.androidx.glance.appWidget)
    implementation(libs.androidx.glance.glance)

    implementation(libs.google.playServicesOssLicenses)

    implementation(platform(libs.google.firebase.firebaseBom))
    implementation(libs.google.firebase.firebaseAnalyticsKtx)
    implementation(libs.google.firebase.firebaseCrashlyticsKtx)
}
