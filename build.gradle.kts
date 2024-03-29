import org.jetbrains.changelog.markdownToHTML

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.8.0"
    id("org.jetbrains.changelog") version "1.3.1"
}

group = "ca.rightsomegoodgames"
version = "1.1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.0")
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    version.set("2022.2")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("221.1")
        untilBuild.set("")

        pluginDescription.set(
            projectDir.resolve("README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").let { markdownToHTML(it) }
        )

        changeNotes.set(provider {
            changelog.run {
                getOrNull(version.get()) ?: getLatest()
            }.toHTML()
        })
    }

    signPlugin {
        certificateChain.set(System.getenv("IDEA_CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("IDEA_PRIVATE_KEY"))
        password.set(System.getenv("IDEA_PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("IDEA_PUBLISH_TOKEN"))
        channels.set(listOf((version as String).split("-").getOrElse(1) { "default" }.split('.').first()))
    }
}
