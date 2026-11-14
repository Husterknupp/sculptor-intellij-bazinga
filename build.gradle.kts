import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.4.21"
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

// Build metadata, set by CI (.github/workflows/build-plugin.yml). Absent in
// local builds, which then keep the plain version and add no change notes.
fun buildEnv(name: String): String? = providers.environmentVariable(name).orNull?.takeIf { it.isNotBlank() }

fun htmlEscape(text: String): String = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

val buildCommit: String? = buildEnv("BUILD_COMMIT")?.take(7)
val buildPrNumber: String? = buildEnv("BUILD_PR_NUMBER")
val buildPrTitle: String? = buildEnv("BUILD_PR_TITLE")
val buildTime: String? = buildEnv("BUILD_TIME")

group = providers.gradleProperty("pluginGroup").get()
// Shown as the plugin version in Settings > Plugins, e.g. 0.1.0-pr7-0736ec2.
version = listOfNotNull(
    providers.gradleProperty("pluginVersion").get(),
    buildPrNumber?.let { "pr$it" },
    buildCommit,
).joinToString("-")

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea(providers.gradleProperty("platformVersion"))
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(25)
}

intellijPlatform {
    pluginConfiguration {
        // Shown in Settings > Plugins > Sculptor DSL > "What's New" / change notes.
        if (buildCommit != null) {
            changeNotes.set(
                listOfNotNull(
                    buildPrNumber?.let { number ->
                        "<b>PR #${htmlEscape(number)}</b>" + (buildPrTitle?.let { ": ${htmlEscape(it)}" } ?: "")
                    },
                    "Commit <code>${htmlEscape(buildCommit)}</code>",
                    buildTime?.let { "Built ${htmlEscape(it)}" },
                ).joinToString("<br>")
            )
        }
        ideaVersion {
            sinceBuild.set(providers.gradleProperty("pluginSinceBuild"))
            untilBuild.set(providers.gradleProperty("pluginUntilBuild"))
        }
    }
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        }
    }

    test {
        useJUnitPlatform()
    }

    buildSearchableOptions {
        enabled = false
    }
}
