package gg.meza.soundsbegone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import gg.meza.soundsbegone.client.ConfigPathResolver;
import org.apache.commons.lang3.SerializationException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static gg.meza.soundsbegone.SoundsBeGoneConfig.MOD_ID;

public class Config {
    private ConfigData configData = new ConfigData();
    private final Path oldConfigPath = ConfigPathResolver.getConfigDir("disabled_sounds.json");
    private final Path configPath = ConfigPathResolver.getConfigDir(MOD_ID + ".json");
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public String lastVersionSeen() {
        return configData.lastVersionSeen;
    }

    public void setLastVersionSeen(String version) {
        configData.lastVersionSeen = version;
        this.saveConfig();
    }

    public boolean isTelemetryEnabled() {
        return configData.telemetry;
    }

    public void toggleTelemetry(boolean toValue) {
        configData.telemetry = toValue;
    }

    public void disableSound(String sound) {
        configData.sounds.add(sound);
    }

    public void enableSound(String sound) {
        configData.sounds.remove(sound);
    }

    public boolean isSoundDisabled(String sound) {
        return configData.sounds.contains(sound);
    }

    public Set<String> disabledSounds() {
        return configData.sounds;
    }

    public void initConfig() {
        renameConfigFile();
        if (Files.exists(configPath)) {
            try {
                BufferedReader reader = Files.newBufferedReader(configPath);
                configData = GSON.fromJson(reader, ConfigData.class);
                reader.close();
            } catch (IOException | JsonParseException e) {
                SoundsBeGoneConfig.LOGGER.error("Cause: " + e.getCause().getClass().getSimpleName());
                if (e.getCause().getClass() == IllegalStateException.class) {
                    convertConfig(configPath);
                    return;
                }
                throw new SerializationException(e);
            }
        }
    }

    public void saveConfig() {
        try {
            // Save config
            BufferedWriter writer = Files.newBufferedWriter(configPath);
            GSON.toJson(configData, writer);
            writer.close();
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    private void renameConfigFile() {
        try {
            if (Files.exists(oldConfigPath)) {
                Files.move(oldConfigPath, configPath);
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    private void convertConfig(Path configPath) {
        try {
            SoundsBeGoneConfig.LOGGER.warn("Old config file found, migrating to new format");
            BufferedReader reader = Files.newBufferedReader(configPath);

            Type setType = new TypeToken<Set<String>>(){}.getType();
            Set<String> disabledSounds = GSON.fromJson(reader, setType);
            reader.close();
            this.configData.sounds = disabledSounds;

            BufferedWriter writer = Files.newBufferedWriter(configPath);
            GSON.toJson(configData, writer);
            writer.close();
        } catch (IOException | JsonParseException e) {
            throw new SerializationException(e);
        }
    }

}
