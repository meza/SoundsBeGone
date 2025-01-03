package gg.meza.client;

import gg.meza.SoundsBeGoneConfig;
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
                    SoundsBeGoneClient.config.saveConfig();
                });
        ConfigCategory general = builder.getOrCreateCategory(Text.of("Played in the last 60 seconds"));
        ConfigCategory disabled = builder.getOrCreateCategory(Text.of("Disabled sounds"));
        ConfigCategory settings = builder.getOrCreateCategory(Text.of("Settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        settings.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable telemetry"), SoundsBeGoneClient.config.isTelemetryEnabled())
                .setDefaultValue(SoundsBeGoneClient.config.isTelemetryEnabled())
                .setSaveConsumer(newValue -> {
                    SoundsBeGoneClient.config.toggleTelemetry(newValue);
                })
                .build());

        settings.addEntry(entryBuilder.startTextDescription(
                Text.of("Enabling telemetry will send anonymous data to help us understand how we can make things better. We DO NOT send any data that would identify you.")
        ).build());
        settings.addEntry(entryBuilder.startTextDescription(Text.of("You can check the telemetry code at https://github.com/meza/SoundsBeGone")).build());
        SubCategoryBuilder telemetrySubcategory = entryBuilder.startSubCategory(Text.of("Telemetry used:"));
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.of(" - Sound enabled/disabled/blocked")).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.of(" - Mod version")).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.of(" - Minecraft version")).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.of(" - Java version")).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.of(" - Operating system used")).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.of(" - Local time")).build());
        settings.addEntry(telemetrySubcategory.build());
        settings.addEntry(entryBuilder.startTextDescription(Text.of("Please consider keeping this on. This data helps development by giving us insight into what is important for the players and what is not")).build());

        SoundsBeGoneClient.SoundMap
                .keySet()
                .stream()
                .filter(s -> !SoundsBeGoneClient.config.isSoundDisabled(s))
                .sorted()
                .forEach((key) -> general.addEntry(constructOption(entryBuilder, key)));

        SoundsBeGoneClient.config.disabledSounds().stream().sorted().forEach((key) -> disabled.addEntry(constructOption(entryBuilder, key)));

        return builder.build();
    }

    private static AbstractConfigListEntry<?> constructOption(ConfigEntryBuilder builder, String key) {
        return builder
                .startBooleanToggle(Text.of(key), SoundsBeGoneClient.config.isSoundDisabled(key))
                .setDefaultValue(SoundsBeGoneClient.config.isSoundDisabled(key))
                .setSaveConsumer(newValue -> {
                    if (newValue) {
                        if (!SoundsBeGoneClient.config.isSoundDisabled(key)) {
                            SoundsBeGoneConfig.LOGGER.info("Disabling sound: " + key);
                            // only do work if necessary
                            SoundsBeGoneClient.config.disableSound(key);
                            SoundsBeGoneClient.telemetry.mutedSound(key);
                        }
                    } else {
                        if (SoundsBeGoneClient.config.isSoundDisabled(key)) {
                            SoundsBeGoneConfig.LOGGER.info("Enabling sound: " + key);
                            // only do work if necessary
                            SoundsBeGoneClient.config.enableSound(key);
                            SoundsBeGoneClient.telemetry.unmutedSound(key);
                        }
                    }
                })
                .setYesNoTextSupplier(bool -> bool ? Text.of("Enable") : Text.of("Disable"))
                .build();
    }
}
