rootProject.name = "klog-logger"

includeBuild("build-logic")
include("klog")
include("klog-slf4j")
include("klog-jpl")
include("klog-sample")

plugins {
    id("com.gradle.enterprise").version("3.15.1")
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

gradleEnterprise {
    if (!System.getenv("CI").isNullOrEmpty()) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
