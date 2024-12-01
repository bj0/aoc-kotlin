rootProject.name = "aoc-kotlin"

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://packages.jetbrains.team/maven/p/amper/amper")
        maven("https://www.jetbrains.com/intellij-repository/releases")
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")    }
}

plugins {
    // apply the plugin:
    id("org.jetbrains.amper.settings.plugin").version("0.5.0")
}

// apply the plugin:
plugins.apply("org.jetbrains.amper.settings.plugin")