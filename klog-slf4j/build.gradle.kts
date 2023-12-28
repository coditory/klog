plugins {
    id("build.kotlin")
    id("build.test")
    id("build.coverage")
    id("build.publishing")
}

description = "Klog connector for Slf4j"

dependencies {
    api(project(":klog"))
    implementation(libs.slf4j.api)
}

publishing {
    publications {
        create<MavenPublication>("jvm") {
            from(components["kotlin"])
            artifact(tasks.named("sourcesJar"))
            pom {
                name.set("klog-slf4j")
                description.set("SLF4J provider implemented with Klog logging library")
            }
        }
    }
}
