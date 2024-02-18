require(project == rootProject) { "build.git is applicable to rootProject only" }

tasks.register<Copy>("installGitHooks") {
    inputs.files("scripts/git/pre-commit")
    outputs.files(".git/hooks/pre-commit")
    from("scripts/git") {
        include("pre-commit")
    }
    into(".git/hooks")
    fileMode = "755".toInt(8)
}
