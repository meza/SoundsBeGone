pluginManagement {
    repositories {
        maven("https://maven.terraformersmc.com/")
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.shedaniel.me/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.5"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    create(rootProject) {
        versions("1.21", "1.21.4")
        vcsVersion = "1.21.4"
        branch("fabric")
        branch("neoforge")
    }
}

rootProject.name = "SoundsBeGone"
