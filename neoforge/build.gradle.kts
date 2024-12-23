@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow")
}

val loader = prop("loom.platform")!!
val minecraftVersion: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}

version = "${mod.version}+mc${minecraftVersion}"
base {
    archivesName.set("${mod.id}-$loader")
}
architectury {
    platformSetupLoomIde()
    neoForge()
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations {
    compileClasspath.get().extendsFrom(commonBundle)
    runtimeClasspath.get().extendsFrom(commonBundle)
    get("developmentNeoForge").extendsFrom(commonBundle)

    configureEach {
        resolutionStrategy.force("net.sf.jopt-simple:jopt-simple:5.0.4")
    }
}

repositories {
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:${common.mod.prop("yarn_mappings")}:v2")
        mappings("dev.architectury:yarn-mappings-patch-neoforge:${common.mod.prop("yarn_mappings_neoforge_patch")}")
    })
    "neoForge"("net.neoforged:neoforge:${common.mod.prop("neoforge_version")}")

    forgeRuntimeLibrary("com.posthog.java:posthog:${common.mod.prop("posthog_version")}")

    modApi("me.shedaniel.cloth:cloth-config-neoforge:${common.mod.prop("cloth_version")}")
    implementation("com.posthog.java:posthog:${common.mod.prop("posthog_version")}")
    include("com.posthog.java:posthog:${common.mod.prop("posthog_version")}")
    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionNeoForge")) { isTransitive = false }
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    accessWidenerPath = rootProject.file("src/main/resources/${mod.id}.accesswidener")

    runConfigs.all {
        isIdeConfigGenerated = true
        runDir = "../../../run"
        vmArgs("-Dmixin.debug.export=true")
    }
}

java {
    val java = if (stonecutter.eval(minecraftVersion, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.jar {
    archiveClassifier = "dev"
}

tasks.remapJar {
    injectAccessWidener = true
    input = tasks.shadowJar.get().archiveFile
    archiveClassifier = null
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(shadowBundle, commonBundle)
    archiveClassifier = "dev-shadow"
    exclude("fabric.mod.json", "architectury.common.json")
}

tasks.processResources {
    properties(listOf("META-INF/neoforge.mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "description" to mod.description,
        "version" to mod.version,
        "minecraftVersion" to minecraftVersion
    )
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

tasks.register<Copy>("buildAndCollect") {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs"))
    dependsOn("build")
}
