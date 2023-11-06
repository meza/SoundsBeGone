package gg.meza.client.forge;

import gg.meza.client.ConfigPathResolver;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ConfigPathResolverImpl implements ConfigPathResolver {

    public static Path getConfigDir(String dir) {
        return FMLPaths.CONFIGDIR.get().resolve(dir);
    }
}
