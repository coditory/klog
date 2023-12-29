plugins {
    id("build.kotlin")
    id("build.test")
    id("build.publishing")
}

description = "Klog connector for Slf4j"

dependencies {
    api(project(":klog"))
    implementation(libs.slf4j.api)
}
