import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("build.java")
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

ktlint {
    version.set(libs.versions.ktlint.get())
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        allWarningsAsErrors = true
    }
}
