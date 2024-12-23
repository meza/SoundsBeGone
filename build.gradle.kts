plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.kikugie.dev/snapshots")
    maven("https://maven.shedaniel.me/")
}

val minecraftVersion = stonecutter.current.version

version = "${mod.version}+mc${minecraftVersion}"
base {
    archivesName.set("${mod.id}-common")
}

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project !in it) null
    else it.prop("loom.platform")
})

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:${prop("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${prop("loader_version")}")
    modApi("me.shedaniel.cloth:cloth-config:${mod.prop("cloth_version")}")
    modImplementation("com.google.code.gson:gson:2.10.1")
    implementation("com.posthog.java:posthog:${mod.prop("posthog_version")}")
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/${mod.id}.accesswidener")

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraftVersion, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}


tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}


tasks.register("configureMinecraft") {
    group = "project"
    val runDir = "${rootProject.projectDir}/run"
    val optionsFile = file("$runDir/options.txt")

    doFirst {

        println("Configuring Minecraft options...")

        // Ensure the run directory exi sts
        optionsFile.parentFile.mkdirs()

        // Write the desired options to 'options.txt'
        optionsFile.writeText(
            """
            guiScale:3
            fov=90
            narrator:0
            soundCategory_music:0.0
            darkMojangStudiosBackground:true
            """.trimIndent()
        )
    }
}

