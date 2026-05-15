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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.meza.soundsbegone.SoundsBeGoneConfig.LOGGER;
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

    public void setInfrequent(String sound, boolean value) {
        if (value) {
            configData.infrequent.add(sound);
        } else {
            configData.infrequent.remove(sound);
        }
    }

    public int getSoundVolume(String sound) {
        return configData.soundVolumes.getOrDefault(sound, 100);
    }

    public void setSoundVolume(String sound, int volume) {
        if (volume == 100) {
            configData.soundVolumes.remove(sound);
        } else {
            configData.soundVolumes.put(sound, volume);
        }
    }

    public boolean isSoundInfrequent(String sound) {
        return configData.infrequent.contains(sound);
    }

    public Set<String> disabledSounds() {
        return configData.soundVolumes.entrySet().stream()
                .filter(e -> e.getValue() == 0)
                .map(Map.Entry::getKey).collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> reducedSounds() {
        return configData.soundVolumes.entrySet().stream()
                .filter(e -> e.getValue() > 0 && e.getValue() < 100)
                .map(Map.Entry::getKey).collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> infrequentSounds() {
        return Set.copyOf(configData.infrequent);
    }

    public double getFrequencyPercentage() {
        return configData.frequencyPercentage;
    }

    public void setFrequencyPercentage(double percentage) {
        configData.frequencyPercentage = percentage;
    }

    public void initConfig() {
        renameConfigFile();
        if (Files.exists(configPath)) {
            try {
                BufferedReader reader = Files.newBufferedReader(configPath);
                configData = GSON.fromJson(reader, ConfigData.class);
                reader.close();
            } catch (IOException | JsonParseException e) {
                Throwable cause = e.getCause();
                LOGGER.error("Failed to load config", e);
                if (cause != null && cause.getClass() == IllegalStateException.class) {
                    convertConfig(configPath);
                    return;
                }
                throw new SerializationException(e);
            }
        }
        migrateLegacySounds();
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
                if (Files.exists(configPath)) {
                    LOGGER.warn("Old config file found, but new config file already exists. Please delete the old config file manually.");
                    return;
                }
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

            Type setType = new TypeToken<Set<String>>() {
            }.getType();
            Set<String> disabledSounds = GSON.fromJson(reader, setType);
            reader.close();
            if (disabledSounds != null) {
                for (String sound : disabledSounds) {
                    this.configData.soundVolumes.putIfAbsent(sound, 0);
                }
            }

            BufferedWriter writer = Files.newBufferedWriter(configPath);
            GSON.toJson(configData, writer);
            writer.close();
        } catch (IOException | JsonParseException e) {
            throw new SerializationException(e);
        }
    }

    private void migrateLegacySounds() {
        if (configData.sounds != null && !configData.sounds.isEmpty()) {
            SoundsBeGoneConfig.LOGGER.info("Migrating legacy disabled sounds to volume map");
            for (String sound : configData.sounds) {
                configData.soundVolumes.putIfAbsent(sound, 0);
            }
            configData.sounds = null;
            saveConfig();
        }
    }

}
