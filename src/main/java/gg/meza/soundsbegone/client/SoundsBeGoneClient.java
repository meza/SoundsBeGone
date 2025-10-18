package gg.meza.soundsbegone.client;

import gg.meza.soundsbegone.Config;
import gg.meza.soundsbegone.telemetry.Telemetry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
/*? if >= 1.21.9 {*/
import gg.meza.soundsbegone.SoundsBeGoneConfig;
import net.minecraft.util.Identifier;
/*?}*/
import org.lwjgl.glfw.GLFW;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoundsBeGoneClient {
    public static Map<String, Date> SoundMap = new ConcurrentHashMap<>();
    public static Config config = new Config();
    public static Telemetry telemetry;

    /*? if >= 1.21.9 {*/
    private static final KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(SoundsBeGoneConfig.MOD_ID, "keybinds"));
    /*?}*/

    public static final KeyBinding openConfig = new KeyBinding(
            "soundsbegone.config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            /*? if >= 1.21.9 {*/
            category
            /*?} else {*/
            /*"soundsbegone.keybinds"
            *//*?}*/
    );


    public static void initClient() {
        config.initConfig();
        telemetry = new Telemetry(MinecraftClient.getInstance());
    }
}
