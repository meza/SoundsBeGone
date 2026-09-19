---
name: Dependency Update
description: MUST CONSULT When asked to update and refresh dependencies in the project.
---

This project uses Stonecutter to manage multiple Minecraft versions and loaders. Please refer to the [Contributing Guide](../../../CONTRIBUTING.md) for more information on how to work with Stonecutter.

When adding or upgrading supported Minecraft versions, follow the [Minecraft Version Update Runbook](../../../docs/minecraft-version-update-runbook.md) instead of this dependency-refresh workflow.

The dependency versions are separated per supported Minecraft version.

## Locating the version files

All the version files are in the `versions/dependencies` folder. Each file is named after the Minecraft version it supports, for example `1.19.4.properties` or `26.2.properties`, and contains the dependencies for that version.

## Looking up a dependency

The versions are used in the `build.gradle.kts` file to determine which dependencies to use for each Minecraft version. The dependencies are defined in the `dependencies` block, and the versions are looked up from the corresponding properties file.

Your sources for available versions are the repositories in the build.gradle.kts file.

## Your task

First determine the requested scope:

- For a named dependency update, update only that dependency in every manifest where it is present.
- For an explicitly requested full dependency refresh, update every dependency in every manifest.

For each dependency and manifest within that scope:

1. Check its current version.
2. Look up the latest compatible version for that manifest's Minecraft version in the repositories defined in `build.gradle.kts`. You might need to look online or on Modrinth to find the correct version-loader combination.
3. Update the version in the manifest.
4. Continue until every dependency and manifest within the requested scope has been handled.
5. Verify the result using the full-matrix build workflow in `CONTRIBUTING.md`.
6. Stop and notify the user.
