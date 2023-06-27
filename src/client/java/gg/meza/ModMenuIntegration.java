package gg.meza;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.text.Text;

import static gg.meza.SoundsBeGoneClient.SoundMap;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            // Return the screen here with the one you created from Cloth Config Builder
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Sounds Be Gone Config"))
                    .setSavingRunnable(() -> {
                        SoundsBeGoneClient.config.saveConfig();
                    });
            ConfigCategory general = builder.getOrCreateCategory(Text.of("Played in the last 60 seconds"));
            ConfigCategory disabled = builder.getOrCreateCategory(Text.of("Disabled sounds"));
            ConfigCategory settings = builder.getOrCreateCategory(Text.of("Settings"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            settings.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable analytics"), SoundsBeGoneClient.config.isAnalyticsEnabled())
                    .setDefaultValue(SoundsBeGoneClient.config.isAnalyticsEnabled())
                    .setSaveConsumer(newValue -> {
                        SoundsBeGoneClient.config.toggleAnalytics(newValue);
                    })
                    .build());

            settings.addEntry(entryBuilder.startTextDescription(
                    Text.of("Enabling analytics will send anonymous data to the server to track how people are using this mod. We DO NOT send any data that would identify you.")
            ).build());
            settings.addEntry(entryBuilder.startTextDescription(Text.of("You can check the analytics code at https://github.com/meza/SoundsBeGone")).build());
            SubCategoryBuilder analyticsSubcategory = entryBuilder.startSubCategory(Text.of("Thing we track:"));
            analyticsSubcategory.add(entryBuilder.startTextDescription(Text.of(" - Sound enabled/disabled/blocked")).build());
            analyticsSubcategory.add(entryBuilder.startTextDescription(Text.of(" - Mod version")).build());
            analyticsSubcategory.add(entryBuilder.startTextDescription(Text.of(" - Minecraft version")).build());
            analyticsSubcategory.add(entryBuilder.startTextDescription(Text.of(" - Java version")).build());
            analyticsSubcategory.add(entryBuilder.startTextDescription(Text.of(" - Operating system used")).build());
            analyticsSubcategory.add(entryBuilder.startTextDescription(Text.of(" - Local time")).build());
            settings.addEntry(analyticsSubcategory.build());
            settings.addEntry(entryBuilder.startTextDescription(Text.of("Please consider keeping this on. This data helps development by giving us insight into what is important to you and what is not")).build());

            SoundMap
                    .keySet()
                    .stream()
                    .filter(s -> !SoundsBeGoneClient.config.isSoundDisabled(s))
                    .sorted()
                    .forEach((key) -> general.addEntry(constructOption(entryBuilder, key)));

            SoundsBeGoneClient.config.disabledSounds().stream().sorted().forEach((key) -> disabled.addEntry(constructOption(entryBuilder, key)));

            return builder.build();
        };
    }

    private AbstractConfigListEntry constructOption(ConfigEntryBuilder builder, String key) {
        return builder
                .startBooleanToggle(Text.of(key), SoundsBeGoneClient.config.isSoundDisabled(key))
                .setDefaultValue(SoundsBeGoneClient.config.isSoundDisabled(key))
                .setSaveConsumer(newValue -> {
                    if (newValue) {
                        if (!SoundsBeGoneClient.config.isSoundDisabled(key)) {
                            SoundsBeGone.LOGGER.info("Disabling sound: " + key);
                            // only do work if necessary
                            SoundsBeGoneClient.config.disableSound(key);
                            SoundsBeGoneClient.analytics.mutedSound(key);
                        }
                    } else {
                        if (SoundsBeGoneClient.config.isSoundDisabled(key)) {
                            SoundsBeGone.LOGGER.info("Enabling sound: " + key);
                            // only do work if necessary
                            SoundsBeGoneClient.config.enableSound(key);
                            SoundsBeGoneClient.analytics.unmutedSound(key);
                        }
                    }
                })
                .setYesNoTextSupplier(bool -> bool ? Text.of("Enable") : Text.of("Disable"))
                .build();
    }
}
