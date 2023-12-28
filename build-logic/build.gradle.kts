plugins {
    `kotlin-dsl`
    kotlin("jvm") version embeddedKotlinVersion
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.kotlin.plugin)
    implementation(libs.gradle.ktlint)
    compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
}
