import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = (findProperty("group") as String?) ?: "com.github.hossein-no1"
version = (findProperty("version") as String?) ?: "1.0.0"

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "library", version.toString())

    pom {
        name = "Compose Multiplatform LineChart"
        description = "Simple, lightweight line chart components for Compose Multiplatform."
        inceptionYear = "2025"
        url = "https://github.com/hossein-no1/compose-multiplatform-linechart/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "hossein-no1"
                name = "Hosen"
                url = "https://github.com/hossein-no1"
            }
        }
        scm {
            url = "https://github.com/hossein-no1/compose-multiplatform-linechart/"
            connection = "scm:git:git://github.com/hossein-no1/compose-multiplatform-linechart.git"
            developerConnection = "scm:git:ssh://git@github.com/hossein-no1/compose-multiplatform-linechart.git"
        }
    }
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    val isMacOs = System.getProperty("os.name").lowercase().contains("mac")
    if (isMacOs) {
        listOf(
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "LineChart"
                isStatic = true
            }
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    namespace = "lib.chart.component"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "lib.chart.component.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "lib.chart.component"
            packageVersion = "1.0.0"
        }
    }
}