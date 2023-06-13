package gg.meza.config;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static gg.meza.SoundsBeGoneClient.DisabledSoundMap;

public class SaveHandler {
    public static void save() {
        try {
            Gson gson = new Gson();
            Path configPath = FabricLoader.getInstance().getConfigDir().resolve("disabled_sounds.json");
            BufferedWriter writer = Files.newBufferedWriter(configPath);
            gson.toJson(DisabledSoundMap, writer);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load() throws IOException {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("disabled_sounds.json");
        if (Files.exists(configPath)) {
            BufferedReader reader = Files.newBufferedReader(configPath);
            Gson gson = new Gson();
            String[] disabledSounds = gson.fromJson(reader, String[].class);
            DisabledSoundMap.addAll(Arrays.asList(disabledSounds));
            reader.close();
        }
    }
}
