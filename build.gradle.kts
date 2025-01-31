plugins {
    // Standard Java/Kotlin
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"

    // IntelliJ Platform Gradle Plugin 2.x
    id("org.jetbrains.intellij.platform") version "2.2.1"
}

group = "com.code2prompt"
version = "1.0.1"

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
        sinceBuild.set("241")
        untilBuild.set("243.*")
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
