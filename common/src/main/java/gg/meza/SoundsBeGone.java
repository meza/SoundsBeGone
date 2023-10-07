package gg.meza;

import gg.meza.analytics.Analytics;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SoundsBeGone {

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("soundsbegone");
    public static final String VERSION = "VERSION_REPL";
    public static final String MOD_ID = "soundsbegone";
    public static Map<String, Date> SoundMap = new HashMap<>();
    public static Config config = new Config();
    public static Analytics analytics;
    public static final KeyBinding openConfig = new KeyBinding("soundsbegone.config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "soundsbegone.keybinds") ;


    public static void initClient() {
        config.initConfig();
        analytics = new Analytics();
    }
}
