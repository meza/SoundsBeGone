# Adding a new Minecraft version to the project

## Preparations

- Check [FabricMC](https://fabricmc.net/blog/) for what changed
- Check [FabricMC](https://fabricmc.net/develop/) for the new fabric related versions. Select your version and copy the contents of the box below on the site
- Check [NeoForge](https://projects.neoforged.net/neoforged/neoforge) for the new NeoForge version
- Check the repositories declared in `build.gradle.kts` for compatible versions of the remaining dependencies

## Steps

Please follow these steps in exact order to add a new Minecraft version to the project.

### 1. Update the `settings.gradle.kts` file

Add the new Minecraft version and its loaders to the `stonecutter.shared` block. It should look like this:

```kotlin
mc("<minecraftVersion>", "fabric", "neoforge")
```

If you're adding support for a **newer** version of Minecraft, update `vcsVersion` to the new canonical target, for example `26.3-fabric`.
When adding an older version, leave `vcsVersion` unchanged.

### 2. Add the dependency manifest

Create `versions/dependencies/<minecraftVersion>.properties` using the previous version's manifest as the structural reference.
Set the Minecraft, Fabric Loader, Fabric API, NeoForge, Mod Menu, Cloth Config, Meza Core, and PostHog versions to compatible published artifacts.

### 3. Build each new target

Set the Fabric target active, then build only that target:

```bash
./gradlew "Set active project to <minecraftVersion>-fabric"
./gradlew buildActive
```

Correct any Fabric incompatibilities and repeat `buildActive` until it passes.

Then set the NeoForge target active and build it:

```bash
./gradlew "Set active project to <minecraftVersion>-neoforge"
./gradlew buildActive
```

Correct any NeoForge incompatibilities and repeat `buildActive` until it passes.
Always switch to a target before editing or testing version-dependent source.
Do not substitute direct `compile*` tasks or version-subproject build tasks.

### 4. Restore the canonical active project

Run:

```bash
./gradlew "Reset active project"
```

This restores the active project to `vcsVersion`, which is the required state for full-matrix verification and handover.

### 5. Verify the full matrix

Run:

```bash
./gradlew build
```

Use `./gradlew buildAndCollect` instead when you also need the jars collected in one place.

If the matrix build exposes a target-specific problem:

1. Run `Set active project to <version>-<loader>` for the failing target.
2. Correct and verify that target with `./gradlew buildActive`.
3. Run `./gradlew "Reset active project"`.
4. Rerun the full-matrix build.

### 6. Restore the handover state

Immediately before handover, run:

```bash
./gradlew "Reset active project"
```

The repository must be left with the active project set to `vcsVersion`.


## Committing your changes

Adding a new version should always use a `feat:` commit.
Make sure the commit includes the new dependency manifest as well as every modified build and source file.


[stonecutter]: https://stonecutter.kikugie.dev/
