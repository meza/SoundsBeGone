import gg.meza.stonecraft.mod

plugins {
    id("gg.meza.stonecraft")
}

val awFile =
    when {
        stonecutter.current.parsed >= "26.1" -> "soundsbegone.accesswidener"
        else -> "soundsbegone.old.accesswidener"
    }

modSettings {
    clientOptions {
        darkBackground = true
        musicVolume = 0.0
        narrator = false
    }

    variableReplacements =
        mapOf(
            "schema" to "\$schema",
            "cloth_version" to mod.prop("cloth_version", "*"),
            "modmenu_version" to mod.prop("modmenu_version", "*"),
            "awFile" to awFile,
        )
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/$awFile")
}

stonecutter {
    swaps["version"] = "private final String MC_VERSION = \"${stonecutter.current.version}\";"
    swaps["loader"] = "private final String LOADER = \"${mod.loader}\";"
    replacements.string(stonecutter.current.parsed < "1.21.11") {
        replace("Identifier", "ResourceLocation")
        replace("ResourceLocationParameter", "ResourceLocationParameter")
    }
}

repositories {
    mavenLocal()
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.meza.gg/releases/")
}

dependencies {
    if (stonecutter.current.parsed >= "26.1") {
        implementation("com.google.code.gson:gson:2.10.1")

        implementation("com.posthog.java:posthog:${mod.prop("posthog_version")}")
        include("com.posthog.java:posthog:${mod.prop("posthog_version")}")

        implementation("gg.meza:meza_core-${mod.loader}:${mod.prop("meza_core_version")}+${stonecutter.current.version}")
        include("gg.meza:meza_core-${mod.loader}:${mod.prop("meza_core_version")}+${stonecutter.current.version}")

        if (mod.isNeoforge) {
            api("me.shedaniel.cloth:cloth-config-neoforge:${mod.prop("cloth_version")}")
            "forgeRuntimeLibrary"("com.posthog.java:posthog:${mod.prop("posthog_version")}")
        }
        if (mod.isFabric) {
            api("me.shedaniel.cloth:cloth-config-fabric:${mod.prop("cloth_version")}") {
                exclude(group = "net.fabricmc.fabric-api")
            }
            if (mod.hasProp("modmenu_version")) {
                api("com.terraformersmc:modmenu:${mod.prop("modmenu_version")}")
            }
        }
    } else {
        add("modImplementation", "com.google.code.gson:gson:2.10.1")
        add("modImplementation", "com.posthog.java:posthog:${mod.prop("posthog_version")}")
        include("com.posthog.java:posthog:${mod.prop("posthog_version")}")

        add("modImplementation", "gg.meza:meza_core-${mod.loader}:${mod.prop("meza_core_version")}+${stonecutter.current.version}")
        include("gg.meza:meza_core-${mod.loader}:${mod.prop("meza_core_version")}+${stonecutter.current.version}")

        if (mod.isNeoforge) {
            add("modApi", "me.shedaniel.cloth:cloth-config-neoforge:${mod.prop("cloth_version")}")
            "forgeRuntimeLibrary"("com.posthog.java:posthog:${mod.prop("posthog_version")}")
        }
        if (mod.isFabric) {
            add("modApi", "me.shedaniel.cloth:cloth-config-fabric:${mod.prop("cloth_version")}") {
                exclude(group = "net.fabricmc.fabric-api")
            }
            if (mod.hasProp("modmenu_version")) {
                add("modApi", "com.terraformersmc:modmenu:${mod.prop("modmenu_version")}")
            }
        }
    }
}

publishMods {
    modrinth {
        if (mod.isFabric) {
            requires("fabric-api")
            optional("modmenu")
        }
        requires("cloth-config")
    }

    curseforge {
        clientRequired = true
        serverRequired = false
        if (mod.isFabric) {
            requires("fabric-api")
            optional("modmenu")
        }
        requires("cloth-config")
    }
}
