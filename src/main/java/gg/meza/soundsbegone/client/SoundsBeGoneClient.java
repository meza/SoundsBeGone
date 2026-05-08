package gg.meza.soundsbegone.client;

import com.mojang.blaze3d.platform.InputConstants;
import gg.meza.soundsbegone.Config;
import gg.meza.soundsbegone.telemetry.Telemetry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
/*? if >= 1.21.9 {*/
import net.minecraft.resources.Identifier;
/*?}*/
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static gg.meza.soundsbegone.SoundsBeGoneConfig.MOD_ID;

public class SoundsBeGoneClient {
    public static Map<String, Date> SoundMap = new ConcurrentHashMap<>();
    public static Config config = new Config();
    public static Telemetry telemetry;
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static SoundEmissionRegulator soundEmissionRegulator = new SoundEmissionRegulator();

    /*? if >= 1.21.9 {*/
    private static final KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "keybinds"));
    /*?}*/

    public static final KeyMapping openConfig = new KeyMapping(
            "soundsbegone.config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            /*? if >= 1.21.9 {*/
            category
            /*?} else {*/
            /*"soundsbegone.keybinds"
            *//*?}*/
    );


    public static void initClient() {
        config.initConfig();
        telemetry = new Telemetry(Minecraft.getInstance());
    }
}
