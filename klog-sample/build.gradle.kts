plugins {
    id("build.kotlin")
    id("com.github.johnrengelman.shadow") version "8.1.1"
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
