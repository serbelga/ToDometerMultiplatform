plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    id("dev.sergiobelda.gradle.base")
    id("dev.sergiobelda.gradle.common.library.android")
    id("dev.sergiobelda.gradle.dependency-graph-generator")
}

kotlin {
    androidTarget()
    jvm("desktop")
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.common.designsystemResources)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
            }
        }
        val commonTest by getting
        val androidMain by getting {
            dependencies {
                implementation(compose.uiTooling)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.uiTooling)
            }
        }
        val desktopTest by getting
        val iosMain by creating
        val iosTest by creating

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}

android {
    namespace = "dev.sergiobelda.todometer.app.common.designsystem"
}
