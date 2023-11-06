package gg.meza.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.meza.SoundsBeGone;

import java.nio.file.Path;

public interface ConfigPathResolver {
    @ExpectPlatform
    static Path getConfigDir(String dir) {
        SoundsBeGone.LOGGER.error("ConfigPathResolver failed to load");
        return null;
    }
}
