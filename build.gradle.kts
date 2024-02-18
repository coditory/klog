plugins {
    id("build.version")
    id("build.git")
    id("build.coverage")
    id("build.publish-root")
}

allprojects {
    group = "com.coditory.klog"
    description = "Async first Kotlin logger"
}

dependencies {
    project(":klog")
    project(":klog-slf4j")
    project(":klog-sample")

    // merged coverage report
    kover(project(":klog"))
    kover(project(":klog-slf4j"))
}
