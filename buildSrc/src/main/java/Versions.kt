object Versions {

    const val compileSdk = 31
    const val minSdk = 21
    const val targetSdk = 31

    const val kotlin = "1.6.10"
    const val androidGradle = "7.1.1"
    const val hilt = "2.40.5"

    const val compose = "1.1.0"
    const val composeNavigation = "2.4.0"
    const val composeViewModel = "2.4.0"

    const val retrofit = "2.9.0"
    const val okhttp = "4.9.1"

    const val accompanist = "0.22.0-rc"

    const val coroutines = "1.5.1"

    const val ksp = "1.6.10-1.0.2"
}

object Deps {

    object Gradle {
        const val android = "com.android.tools.build:gradle:${Versions.androidGradle}"
        const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val hiltAndroidPlugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
    }

    object Compose {
        const val ui = "androidx.compose.ui:ui:${Versions.compose}"
        const val tooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
        const val preview = "androidx.compose.ui:ui-tooling-preview:${Versions.compose}"
        const val test = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"
        const val material = "androidx.compose.material:material:${Versions.compose}"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.composeViewModel}"
        const val navigation = "androidx.navigation:navigation-compose:${Versions.composeNavigation}"
        const val animation = "androidx.compose.animation:animation:${Versions.compose}"

        object Accompanist {
            const val animation = "com.google.accompanist:accompanist-navigation-animation:${Versions.accompanist}"
            const val systemUiController = "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}"
            const val swipeRefresh = "com.google.accompanist:accompanist-swiperefresh:${Versions.accompanist}"
            const val pager = "com.google.accompanist:accompanist-pager:${Versions.accompanist}"
            const val pagerIndicator = "com.google.accompanist:accompanist-pager-indicators:${Versions.accompanist}"
            const val inset = "com.google.accompanist:accompanist-insets:${Versions.accompanist}"
        }

        object Misc {
            const val activityCompose = "androidx.activity:activity-compose:1.4.0"
            const val paging = "androidx.paging:paging-compose:1.0.0-alpha13"
            const val material3 = "androidx.compose.material3:material3:1.0.0-alpha01"
            const val materialIconsExtended = "androidx.compose.material:material-icons-extended:1.1.0-alpha01"
        }
    }

    object Hilt {
        const val android = "com.google.dagger:hilt-android:${Versions.hilt}"
        const val compiler = "com.google.dagger:hilt-compiler:${Versions.hilt}"
        const val navigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0"
    }

    object Retrofit {
        const val main = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    }

    object OkHttp {
        const val main = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
        const val logging = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    }

    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    }
}