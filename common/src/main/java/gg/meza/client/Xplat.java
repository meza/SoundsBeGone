package gg.meza.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.meza.SoundsBeGone;

import java.nio.file.Path;

public interface Xplat {
    @ExpectPlatform
    static Path getConfigDir(String dir) {
        SoundsBeGone.LOGGER.error("Xplat failed to load");
        return null;
    }
}
