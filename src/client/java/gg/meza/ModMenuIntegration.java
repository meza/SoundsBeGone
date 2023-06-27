package gg.meza;

import com.google.gson.Gson;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.apache.commons.lang3.SerializationException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static gg.meza.SoundsBeGoneClient.DisabledSoundMap;
import static gg.meza.SoundsBeGoneClient.SoundMap;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()  {
        return parent -> {
            // Return the screen here with the one you created from Cloth Config Builder
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Sounds Be Gone Config"))
                    .setSavingRunnable(()  -> {
                        try {
                            // Save config
                            Gson gson = new Gson();
                            Path configPath = FabricLoader.getInstance().getConfigDir().resolve("disabled_sounds.json");
                            BufferedWriter writer = Files.newBufferedWriter(configPath);
                            gson.toJson(SoundsBeGoneClient.DisabledSoundMap, writer);
                            writer.close();
                        } catch (IOException e) {
                            throw new SerializationException(e);
                        }

                    });
            ConfigCategory general = builder.getOrCreateCategory(Text.of("Played in the last 60 seconds"));
            ConfigCategory disabled = builder.getOrCreateCategory(Text.of("Disabled sounds"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            SoundMap
                    .keySet()
                    .stream()
                    .filter(s -> !SoundsBeGoneClient.DisabledSoundMap.contains(s))
                    .sorted()
                    .forEach((key) -> general.addEntry(constructOption(entryBuilder, key)));

            DisabledSoundMap.stream().sorted().forEach((key) -> disabled.addEntry(constructOption(entryBuilder, key)));

            return builder.build();
        };
    }

    private AbstractConfigListEntry constructOption(ConfigEntryBuilder builder, String key) {
        return builder
                .startBooleanToggle(Text.of(key), SoundsBeGoneClient.DisabledSoundMap.contains(key))
                .setDefaultValue(SoundsBeGoneClient.DisabledSoundMap.contains(key))
                .setSaveConsumer(newValue -> {
                    if (newValue) {
                        SoundsBeGoneClient.DisabledSoundMap.add(key);
                        SoundsBeGoneClient.analytics.mutedSound(key);
                    } else {
                        SoundsBeGoneClient.DisabledSoundMap.remove(key);
                        SoundsBeGoneClient.analytics.unmutedSound(key);
                    }
                })
                .setYesNoTextSupplier(bool -> bool ? Text.of("Enable") : Text.of("Disable"))
                .build();
    }
}
