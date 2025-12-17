buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.commonmark:commonmark:0.22.0")
    }
}

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

plugins {
    // Standard Java/Kotlin
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.20"

    // IntelliJ Platform Gradle Plugin 2.x
    id("org.jetbrains.intellij.platform") version "2.2.1"
    
    // Changelog Plugin
    id("org.jetbrains.changelog") version "2.2.1"
}

group = "com.code2prompt"
version = "1.0.4" // Updated version

// If you’re using gradle.properties for platformType and platformVersion, e.g.:
//   platformType=IC
//   platformVersion=2024.3.1
// then you can read them here.

repositories {
    mavenCentral()
    maven {
        url = uri("https://www.jetbrains.com/intellij-repository/releases/")
    }
    maven {
        url = uri("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
    }
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    // This block replaces the old `intellij { ... }` block from the 1.x plugin.
    intellijPlatform {
        // Read platformType + platformVersion from gradle.properties
        val type = providers.gradleProperty("platformType")
        val version = providers.gradleProperty("platformVersion")

        // e.g. create("IC", "2024.3.1") or create(type, version)
        // This sets up the IntelliJ-based IDE distribution you build against.
        create(type, version)

        // Add any bundled plugin dependencies or marketplace plugins:
        // For example, the Terminal plugin ID is "org.jetbrains.plugins.terminal"
//        bundledPlugin("org.jetbrains.plugins.terminal")
    }

    // Kotlin standard library
    implementation(kotlin("stdlib"))
}

// If you have tasks like patchPluginXml, signPlugin, or publishPlugin,
// you’ll want to revisit them – 2.x reorganizes some tasks into sub-plugins.
// For a simple plugin, though, you can often omit them or keep them minimal.

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }

    patchPluginXml {
        val readmeContent = providers.fileContents(layout.projectDirectory.file("README.md")).asBytes.map {
            val text = String(it, Charsets.UTF_8)
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"
            val lines = text.lines()
            if (!lines.contains(start) || !lines.contains(end)) {
                throw GradleException("README.md must contain '$start' and '$end'")
            }
            val content = lines.subList(lines.indexOf(start) + 1, lines.indexOf(end)).joinToString("\n")
            val parser = Parser.builder().build()
            val renderer = HtmlRenderer.builder().build()
            val document = parser.parse(content)
            renderer.render(document)
        }
        pluginDescription.set(readmeContent)

        val currentVersion = project.version.toString()
        val changelogContent = providers.fileContents(layout.projectDirectory.file("CHANGELOG.md")).asBytes.map {
            val text = String(it, Charsets.UTF_8)
            val lines = text.lines()
            val version = currentVersion
            
            // Find the start line: "### ... Version 1.0.4 ..."
            val startLineIndex = lines.indexOfFirst { line -> 
                line.trim().startsWith("###") && line.contains("Version $version") 
            }
            
            if (startLineIndex == -1) {
                return@map "" // Version not found
            }

            // Find the end line: The next "###" header, or end of file
            // We search starting from the line AFTER the start line
            val remainingLines = lines.drop(startLineIndex + 1)
            val endLineIndex = remainingLines.indexOfFirst { line -> 
                line.trim().startsWith("###") 
            }
            
            val rawNotes = if (endLineIndex == -1) {
                remainingLines
            } else {
                remainingLines.subList(0, endLineIndex)
            }.joinToString("\n").trim()

            val parser = Parser.builder().build()
            val renderer = HtmlRenderer.builder().build()
            val document = parser.parse(rawNotes)
            renderer.render(document)
        }
        changeNotes.set(changelogContent)

        sinceBuild.set("251") // Keep compatible with older versions if desired
        untilBuild.set("253.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}