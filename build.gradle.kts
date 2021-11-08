buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(ClassPaths.gradlePlugin)
        classpath(ClassPaths.kotlinPlugin)
        classpath(ClassPaths.spotlessPlugin)
        classpath(ClassPaths.daggerPlugin)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}