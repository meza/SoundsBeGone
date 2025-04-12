package gg.meza.soundsbegone.client;

/*? if fabric {*/
import net.fabricmc.loader.api.FabricLoader;
/*?}*/

/*? if neoforge {*/
/*import net.neoforged.fml.loading.FMLPaths;
*//*?}*/

import java.nio.file.Path;

public class ConfigPathResolver {
    public static Path getConfigDir(String dir) {
        /*? if fabric {*/
        return FabricLoader.getInstance().getConfigDir().resolve(dir);
        /*?}*/

        /*? if neoforge {*/
        /*return FMLPaths.CONFIGDIR.get().resolve(dir);
        *//*?}*/
    }
}
