plugins {
    id("build.kotlin") apply false
    id("build.version")
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
}

description = "Klog - Async first JVM logger based on kotlin channels"

dependencies {
    kover(project(":klog"))
    kover(project(":klog-slf4j"))
}
