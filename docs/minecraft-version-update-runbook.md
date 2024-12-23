# Adding a new Minecraft version to the project

## Preparations

- Check [FabricMC](https://fabricmc.net/blog/) for what changed
- Check [FabricMC](https://fabricmc.net/develop/) for the new fabric related versions. Select your version and copy the contents of the box below on the site
- Check [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/) for the new forge related versions

## Steps

Please follow these steps in exact order to add a new Minecraft version to the project.

### 1. Update the `settings.gradle.kts` file

Add the new Minecraft version in the `versions()` list. It should look something like this: `versions("1.20.1", "1.20.4", "1.20.6")`

If you're adding support for an **older** version of Minecraft, this is enough.

If you're adding support for a **newer** version of Minecraft, you need to update the `vcsVersion` to the new version number.

### 2. Update the `versions/[minecraftVersion]/gradle.properties` file

The previous step created a new folder in the `versions` directory. Go to the folder with the new version and update the `gradle.properties`
file with the version numbers you've gathered in the preparations step.

If it failed to create said folder, you can create it manually.

The block you copied from the FabricMC site should require no changes, paste as is.
For the rest, please refer to the existing files in the other versions.

### 3. Set active project

Once you've done all the previous steps, there will be a "Set active project to..." task corresponding to your new version
in your IDE. Run it to set the active project to the new version.

### 4. Run the build and see what breaks

Run `./gradlew chiseledBuild` to build the project. This will take a while, so grab a coffee.


## Committing your changes

Adding a new version should always be a `feat:` commit.

```bash
git commit -am'feat: Add support for Minecraft 1.20.1'
```


[stonecutter]: https://stonecutter.kikugie.dev/
