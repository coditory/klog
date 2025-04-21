plugins {
    id("build.kotlin")
    id("build.test")
    id("build.publish")
}

description = "Klog connector for Java Platform Logging (System.Logger)"

dependencies {
    api(project(":klog"))
}
