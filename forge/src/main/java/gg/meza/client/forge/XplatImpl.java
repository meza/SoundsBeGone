package gg.meza.client.forge;

import gg.meza.client.Xplat;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class XplatImpl implements Xplat {

    public static Path getConfigDir(String dir) {
        return FMLPaths.CONFIGDIR.get().resolve(dir);
    }
}
