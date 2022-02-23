plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version Versions.ksp
    id("dagger.hilt.android.plugin")
}

kotlin {
    sourceSets {
        named("debug") {
            kotlin.srcDir("build/generated/ksp/debug/kotlin")
        }
        named("release") {
            kotlin.srcDir("build/generated/ksp/release/kotlin")
        }
    }
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "com.zyc.wan"
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerVersion = "1.5.21"
        kotlinCompilerExtensionVersion = Versions.compose
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }
}

dependencies {

    with(Deps.Compose) {
        implementation(ui)
        implementation(tooling)
        implementation(preview)
        implementation(material)
        implementation(animation)
        implementation(test)
        implementation(viewModel)
        implementation(navigation)

        with(Deps.Compose.Accompanist) {
            implementation(animation)
            implementation(systemUiController)
            implementation(swipeRefresh)
            implementation(pager)
            implementation(pagerIndicator)
            implementation(inset)
        }

        with(Deps.Compose.Misc) {
            implementation(activityCompose)
            implementation(paging)
            implementation(material3)
            implementation(materialIconsExtended)
        }
    }

    with(Deps.Hilt) {
        implementation(android)
        kapt(compiler)
        implementation(navigationCompose)
    }

    with(Deps.Coroutines) {
        implementation(core)
        implementation(android)
    }

    with(Deps.Retrofit) {
        implementation(main)
        implementation(gson)
    }

    with(Deps.OkHttp) {
        implementation(main)
        implementation(logging)
    }

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.core:core-splashscreen:1.0.0-alpha02")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")

    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("com.google.android.material:material:1.4.0")
    implementation("io.arrow-kt:arrow-core:1.0.1")

    implementation("com.google.guava:guava:26.0-android")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

    implementation("io.github.raamcosta.compose-destinations:animations-core:1.1.2-beta")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.1.2-beta")
}
