@file:Suppress("UnstableApiUsage")

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.kikugie.dev/snapshots")
    maven("https://maven.shedaniel.me/")
}

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow")
}

val minecraftVersion: String = stonecutter.current.version
val loader = prop("loom.platform")!!
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}
version = "${mod.version}+mc${minecraftVersion}"
base {
    archivesName.set("${mod.id}-$loader")
}
architectury {
    platformSetupLoomIde()
    fabric()
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
    get("developmentFabric").extendsFrom(commonBundle)
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:${common.mod.prop("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${common.mod.prop("loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${common.mod.prop("fabric_version")}")
    modApi("com.terraformersmc:modmenu:${common.mod.prop("modmenu_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    modApi("me.shedaniel.cloth:cloth-config-fabric:${common.mod.prop("cloth_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    implementation("com.posthog.java:posthog:${common.mod.prop("posthog_version")}")
    include("com.posthog.java:posthog:${common.mod.prop("posthog_version")}")

    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionFabric")) { isTransitive = false }
}

loom {

    accessWidenerPath = rootProject.file("src/main/resources/${mod.id}.accesswidener")

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

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

tasks.shadowJar {
    configurations = listOf(shadowBundle, commonBundle)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    injectAccessWidener = true
    input = tasks.shadowJar.get().archiveFile
    archiveClassifier = null
    dependsOn(tasks.shadowJar)
}

tasks.jar {
    archiveClassifier = "dev"
}

tasks.processResources {
    properties(listOf("fabric.mod.json"),
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
