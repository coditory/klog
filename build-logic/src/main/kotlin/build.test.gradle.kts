@file:Suppress("UnstableApiUsage", "HasPlatformType")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm")
    id("build.coverage")
    id("com.adarshr.test-logger")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

testing {
    suites {
        val test by getting(JvmTestSuite::class)

        val integrationTest by registering(JvmTestSuite::class) {
            testType.set(TestSuiteType.INTEGRATION_TEST)

            val mainSourceSet = project.sourceSets.main.get()
            val testSourceSet = project.sourceSets.test.get()

            sources {
                compileClasspath += testSourceSet.output +
                    mainSourceSet.output + testSourceSet.compileClasspath
                runtimeClasspath += testSourceSet.output +
                    mainSourceSet.output + testSourceSet.runtimeClasspath
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
}

kotlin {
    target.compilations {
        getByName("integrationTest")
            .associateWith(getByName("test"))
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.STANDARD_ERROR,
            TestLogEvent.STANDARD_OUT,
            TestLogEvent.SKIPPED,
            TestLogEvent.PASSED,
        )
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
}

dependencies {
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.awaitility)
}
