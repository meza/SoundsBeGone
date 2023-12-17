package gg.meza.client.neoforge;

import gg.meza.client.ConfigPathResolver;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ConfigPathResolverImpl implements ConfigPathResolver {

    public static Path getConfigDir(String dir) {
        return FMLPaths.CONFIGDIR.get().resolve(dir);
    }
}
