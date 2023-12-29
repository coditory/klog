plugins {
    id("build.kotlin")
    alias(libs.plugins.shadowJar)
    application
}

application {
    mainClass.set("com.coditory.klog.sample.SampleRunner")
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClass.get(),
            ),
        )
    }
}

dependencies {
    implementation(project(":klog-slf4j"))
}
