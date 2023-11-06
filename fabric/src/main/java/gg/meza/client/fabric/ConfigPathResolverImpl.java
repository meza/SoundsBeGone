package gg.meza.client.fabric;

import gg.meza.client.ConfigPathResolver;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ConfigPathResolverImpl implements ConfigPathResolver {

    public static Path getConfigDir(String dir) {
        return FabricLoader.getInstance().getConfigDir().resolve(dir);
    }
}
