package gg.meza;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import gg.meza.client.ConfigPathResolver;
import org.apache.commons.lang3.SerializationException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class Config {
    private ConfigData configData = new ConfigData();
    private Path configPath = ConfigPathResolver.getConfigDir("disabled_sounds.json");

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
        if (Files.exists(configPath)) {
            try {
                BufferedReader reader = Files.newBufferedReader(configPath);
                Gson gson = new Gson();
                configData = gson.fromJson(reader, ConfigData.class);
                reader.close();
            } catch (IOException | JsonParseException e) {
                SoundsBeGone.LOGGER.error("Cause: " + e.getCause().getClass().getSimpleName());
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
            Gson gson = new Gson();
            BufferedWriter writer = Files.newBufferedWriter(configPath);
            gson.toJson(configData, writer);
            writer.close();
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    private void convertConfig(Path configPath) {
        try {
            SoundsBeGone.LOGGER.warn("Old config file found, migrating to new format");
            BufferedReader reader = Files.newBufferedReader(configPath);
            Gson gson = new Gson();
            Set<String> disabledSounds = gson.fromJson(reader, Set.class);
            reader.close();
            this.configData.sounds = disabledSounds;

            BufferedWriter writer = Files.newBufferedWriter(configPath);
            gson.toJson(configData, writer);
            writer.close();
        } catch (IOException | JsonParseException e) {
            throw new SerializationException(e);
        }
    }

}
