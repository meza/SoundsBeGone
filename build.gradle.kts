import gg.meza.stonecraft.mod

plugins {
    id("gg.meza.stonecraft")
}

modSettings {
    clientOptions {
        darkBackground = true
        musicVolume = 0.0
        narrator = false
    }

    variableReplacements = mapOf(
        "schema" to "\$schema",
        "cloth_version" to mod.prop("cloth_version"),
        "modmenu_version" to mod.prop("modmenu_version")
    )
}

repositories {
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.shedaniel.me/")
}

dependencies {
    modImplementation("com.google.code.gson:gson:2.10.1")
    modImplementation("com.posthog.java:posthog:${mod.prop("posthog_version")}")

    include("com.posthog.java:posthog:${mod.prop("posthog_version")}")

    if (mod.isNeoforge) {
        modApi("me.shedaniel.cloth:cloth-config-neoforge:${mod.prop("cloth_version")}")
        "forgeRuntimeLibrary"("com.posthog.java:posthog:${mod.prop("posthog_version")}")
    }
    if (mod.isFabric) {
        modApi("me.shedaniel.cloth:cloth-config-fabric:${mod.prop("cloth_version")}") {
            exclude(group = "net.fabricmc.fabric-api")
        }
        modApi("com.terraformersmc:modmenu:${mod.prop("modmenu_version")}")
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
        if (mod.isFabric) {
            requires("fabric-api")
            optional("modmenu")
        }
        requires("cloth-config")
    }
}
