package gg.meza.client.fabric;

import gg.meza.client.Xplat;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class XplatImpl implements Xplat {

    public static Path getConfigDir(String dir) {
        return FabricLoader.getInstance().getConfigDir().resolve(dir);
    }
}
