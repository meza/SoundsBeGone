pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.terraformersmc.com/")
        maven("https://maven.shedaniel.me/")
    }
}

plugins {
    id("gg.meza.stonecraft") version "1.6.0"
    id("dev.kikugie.stonecutter") version "0.6.2"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    shared {
        fun mc(version: String, vararg loaders: String) {
            for (it in loaders) vers("$version-$it", version)
        }

        mc("1.19.4", "fabric")
        mc("1.21", "fabric", "neoforge")
        mc("1.21.4", "fabric", "neoforge")
        mc("1.21.5", "fabric", "neoforge")
        mc("1.21.6", "fabric", "neoforge")
        mc("1.21.9", "fabric", "neoforge")

        vcsVersion = "1.21.9-fabric"
    }
    create(rootProject)
}

rootProject.name = "SoundsBeGone"
