plugins {
    id("build.kotlin")
    id("build.test")
    id("build.coverage")
}

description = "Klog - JVM logger based on kotlin channels"

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.kotlin.reflect)
}
