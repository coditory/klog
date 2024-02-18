plugins {
    id("build.kotlin") apply false
    id("build.version")
    id("build.git")
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
    alias(libs.plugins.nexusPublish)
}

allprojects {
    group = "com.coditory.klog"
    description = "Async first Kotlin logger"
}

dependencies {
    kover(project(":klog"))
    kover(project(":klog-slf4j"))
}

tasks.register("coverage") {
    dependsOn("koverXmlReport", "koverHtmlReport", "koverLog")
}

nexusPublishing {
    repositories {
        sonatype {
            System.getenv("OSSRH_STAGING_PROFILE_ID")?.let { stagingProfileId = it }
            System.getenv("OSSRH_USERNAME")?.let { username.set(it) }
            System.getenv("OSSRH_PASSWORD")?.let { password.set(it) }
        }
    }
}
