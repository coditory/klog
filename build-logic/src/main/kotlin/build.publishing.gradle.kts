import gradle.kotlin.dsl.accessors._65a529fd93476750f091a5ac34bc1965.javadoc

plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "com.coditory.klog"

publishing {
    repositories {
        maven {
            name = "snapshots"
            setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
        maven {
            name = "releases"
            setUrl("https://s01.oss.sonatype.org/content/repositories/releases/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }

    publications.withType<MavenPublication> {
        pom {
            name.set("klog")
            description.set(project.description ?: rootProject.description ?: "Kotlin logging library")
            url.set("https://github.com/coditory/klog")
            organization {
                name = "Coditory"
                url = "https://coditory.com"
            }
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("ogesaku")
                    name.set("ogesaku")
                    email.set("ogesaku@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/coditory/klog.git")
                url.set("https://github.com/coditory/klog")
            }
            issueManagement {
                system.set("GitHub")
                url.set("https://github.com/coditory/klog/issues")
            }
        }
    }
}

signing {
    if (System.getenv("SIGNING_KEY")?.isNotBlank() == true && System.getenv("SIGNING_PASSWORD")?.isNotBlank() == true) {
        useInMemoryPgpKeys(System.getenv("SIGNING_KEY"), System.getenv("SIGNING_PASSWORD"))
    }
    sign(publishing.publications)
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
