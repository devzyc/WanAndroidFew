buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        with(Deps.Gradle) {
            classpath(android)
            classpath(kotlinPlugin)
            classpath(hiltAndroidPlugin)
        }
    }
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildDir)
}