import java.io.IOException

allprojects {
    val textVersion = project.version.toString()
    if (textVersion == "unspecified") {
        val useReleaseVersion = project.gradle.startParameter.taskNames
            .any { it.startsWith("publish") && it.endsWith("PublicationToReleaseRepository") }
        version = if (useReleaseVersion)
            Semver.fromGitTag().nextPatchRelease().toString()
        else
            Semver.fromGitTag().nextPatchSnapshot().toString()
    } else {
        Semver.parse(textVersion)
            ?: "Could not parse project version: $textVersion"
    }
}

tasks.register("version") {
    doLast {
        println(project.version)
    }
}

tasks.register("nextReleaseVersion") {
    doLast {
        println(Semver.fromGitTag().nextPatchRelease().toString())
    }
}

data class Semver(
    private val major: Int,
    private val minor: Int,
    private val patch: Int,
    private val suffix: String? = null,
) : Comparable<Semver> {
    override fun compareTo(other: Semver): Int {
        if (major > other.major) return 1
        if (major < other.major) return -1
        if (minor > other.minor) return 1
        if (minor < other.minor) return -1
        if (patch > other.patch) return 1
        if (patch < other.patch) return -1
        if (suffix != null && other.suffix == null) return 1
        if (suffix == null && other.suffix != null) return -1
        if (suffix != null && other.suffix != null) return suffix.compareTo(other.suffix)
        return 0
    }

    fun nextPatchRelease(): Semver {
        return copy(
            patch = patch + 1,
            suffix = null
        )
    }

    fun nextPatchSnapshot(): Semver {
        return copy(
            patch = patch + 1,
            suffix = "SNAPSHOT"
        )
    }

    override fun toString(): String {
        return if (suffix.isNullOrEmpty()) {
            "$major.$minor.$patch"
        } else {
            "$major.$minor.$patch-$suffix"
        }
    }

    fun tagName(): String {
        return "v${toString()}"
    }

    companion object {
        private val REGEX = Regex("v?([0-9]+)\\.([0-9]+)\\.([0-9]+)(-.+)?")

        fun parse(text: String): Semver? {
            val groups = REGEX.matchEntire(text)?.groups ?: return null
            if (groups.size < 3 || groups.size > 4) return null
            return Semver(
                major = groups[0]?.value?.toIntOrNull() ?: return null,
                minor = groups[1]?.value?.toIntOrNull() ?: return null,
                patch = groups[2]?.value?.toIntOrNull() ?: return null,
                suffix = groups[3]?.value,
            )
        }

        fun fromGitTag(): Semver {
            return runCommand("git tag -l 'v[0-9]*.[0-9]*.[0-9]*' --sort=-v:refname | head -n 1")
                .split('\n')
                .mapNotNull { parse(it) }
                .minOrNull()
                ?: Semver(0, 0, 0)
        }

        fun runCommand(
            command: String,
            workingDir: File = File("."),
            timeoutAmount: Long = 60,
            timeoutUnit: TimeUnit = TimeUnit.SECONDS
        ): String = ProcessBuilder("sh", "-c", command)
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
            .apply { waitFor(timeoutAmount, timeoutUnit) }
            .run {
                val error = errorStream.bufferedReader().readText().trim()
                if (error.isNotEmpty()) {
                    throw IOException(error)
                }
                inputStream.bufferedReader().readText().trim()
            }
    }
}
