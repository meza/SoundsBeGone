package gg.meza.client;

import gg.meza.SoundsBeGone;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {

    public static Screen getConfigeScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("Sounds Be Gone Config"))
                .setSavingRunnable(() -> {
                    SoundsBeGone.config.saveConfig();
                });
        ConfigCategory general = builder.getOrCreateCategory(Text.of("Played in the last 60 seconds"));
        ConfigCategory disabled = builder.getOrCreateCategory(Text.of("Disabled sounds"));
        ConfigCategory settings = builder.getOrCreateCategory(Text.of("Settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        settings.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable analytics"), SoundsBeGone.config.isAnalyticsEnabled())
                .setDefaultValue(SoundsBeGone.config.isAnalyticsEnabled())
                .setSaveConsumer(newValue -> {
                    SoundsBeGone.config.toggleAnalytics(newValue);
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

        SoundsBeGone.SoundMap
                .keySet()
                .stream()
                .filter(s -> !SoundsBeGone.config.isSoundDisabled(s))
                .sorted()
                .forEach((key) -> general.addEntry(constructOption(entryBuilder, key)));

        SoundsBeGone.config.disabledSounds().stream().sorted().forEach((key) -> disabled.addEntry(constructOption(entryBuilder, key)));

        return builder.build();
    }

    private static AbstractConfigListEntry<?> constructOption(ConfigEntryBuilder builder, String key) {
        return builder
                .startBooleanToggle(Text.of(key), SoundsBeGone.config.isSoundDisabled(key))
                .setDefaultValue(SoundsBeGone.config.isSoundDisabled(key))
                .setSaveConsumer(newValue -> {
                    if (newValue) {
                        if (!SoundsBeGone.config.isSoundDisabled(key)) {
                            SoundsBeGone.LOGGER.info("Disabling sound: " + key);
                            // only do work if necessary
                            SoundsBeGone.config.disableSound(key);
                            SoundsBeGone.analytics.mutedSound(key);
                        }
                    } else {
                        if (SoundsBeGone.config.isSoundDisabled(key)) {
                            SoundsBeGone.LOGGER.info("Enabling sound: " + key);
                            // only do work if necessary
                            SoundsBeGone.config.enableSound(key);
                            SoundsBeGone.analytics.unmutedSound(key);
                        }
                    }
                })
                .setYesNoTextSupplier(bool -> bool ? Text.of("Enable") : Text.of("Disable"))
                .build();
    }
}
