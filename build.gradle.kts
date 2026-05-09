import gg.meza.stonecraft.mod

plugins {
    id("gg.meza.stonecraft")
}

val isDeobfuscated = stonecutter.current.parsed >= "26.1"

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
        )
}

stonecutter {
    swaps["version"] = "String MC_VERSION = \"${stonecutter.current.version}\";"
    swaps["loader"] = "String LOADER = \"${mod.loader}\";"
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
    val implementationConfiguration =
        when {
            isDeobfuscated -> "implementation"
            else -> "modImplementation"
        }
    val apiConfiguration =
        when {
            isDeobfuscated -> "api"
            else -> "modApi"
        }

    val posthog = "com.posthog.java:posthog:${mod.prop("posthog_version")}"
    val mezaCore = "gg.meza:meza_core-${mod.loader}:${mod.prop("meza_core_version")}+${stonecutter.current.version}"

    add(implementationConfiguration, "com.google.code.gson:gson:2.10.1")
    add(implementationConfiguration, posthog)
    include(posthog)

    add(implementationConfiguration, mezaCore)
    include(mezaCore)

    if (mod.isNeoforge) {
        add(apiConfiguration, "me.shedaniel.cloth:cloth-config-neoforge:${mod.prop("cloth_version")}")
        "forgeRuntimeLibrary"(posthog)
    }
    if (mod.isFabric) {
        add(apiConfiguration, "me.shedaniel.cloth:cloth-config-fabric:${mod.prop("cloth_version")}") {
            exclude(group = "net.fabricmc.fabric-api")
        }
        if (mod.hasProp("modmenu_version")) {
            add(apiConfiguration, "com.terraformersmc:modmenu:${mod.prop("modmenu_version")}")
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
