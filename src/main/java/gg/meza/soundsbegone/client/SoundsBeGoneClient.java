package gg.meza.soundsbegone.client;

import gg.meza.soundsbegone.Config;
import gg.meza.soundsbegone.telemetry.Telemetry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoundsBeGoneClient {
    public static Map<String, Date> SoundMap = new ConcurrentHashMap<>();
    public static Config config = new Config();
    public static Telemetry telemetry;
    public static final KeyBinding openConfig = new KeyBinding(
            "soundsbegone.config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "soundsbegone.keybinds"
    );


    public static void initClient() {
        config.initConfig();
        telemetry = new Telemetry(MinecraftClient.getInstance());
    }
}
