package gg.meza.soundsbegone.client;

import gg.meza.soundsbegone.SoundState;
import gg.meza.soundsbegone.SoundsBeGoneConfig;

import gg.meza.supporters.clothconfig.SupportCategory;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ConfigScreen {
    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("soundsbegone.config.title"))
                .setSavingRunnable(() -> {
                    SoundsBeGoneClient.config.saveConfig();
                });
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("soundsbegone.config.category.latest"));
        ConfigCategory allSounds = builder.getOrCreateCategory(Component.translatable("soundsbegone.config.category.all_sounds"));
        ConfigCategory disabled = builder.getOrCreateCategory(Component.translatable("soundsbegone.config.category.disabled"));
        ConfigCategory infrequent = builder.getOrCreateCategory(Component.translatable("soundsbegone.config.category.infrequent"));
        ConfigCategory settings = builder.getOrCreateCategory(Component.translatable("soundsbegone.config.category.settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        infrequent.addEntry(
                entryBuilder.startDoubleField(Component.translatable("soundsbegone.config.infrequent.frequency.title"), SoundsBeGoneClient.config.getFrequencyPercentage())
                        .setMin(0.001)
                        .setMax(99.999)
                        .setTooltip(Component.translatable("soundsbegone.config.infrequent.frequency.tooltip"))
                        .setSaveConsumer(newValue -> {
                            SoundsBeGoneClient.config.setFrequencyPercentage(newValue);
                        })
                        .setDefaultValue(SoundsBeGoneClient.config.getFrequencyPercentage())
                        .build()
        );

        settings.addEntry(entryBuilder.startBooleanToggle(Component.translatable("soundsbegone.config.telemetry.switch"), SoundsBeGoneClient.config.isTelemetryEnabled())
                .setDefaultValue(SoundsBeGoneClient.config.isTelemetryEnabled())
                .setSaveConsumer(newValue -> {
                    SoundsBeGoneClient.config.toggleTelemetry(newValue);
                })
                .build());

        settings.addEntry(entryBuilder.startTextDescription(Component.translatable("soundsbegone.config.telemetry.description")).build());
        settings.addEntry(entryBuilder.startTextDescription(Component.translatable("soundsbegone.config.telemetry.verify", "https://github.com/meza/SoundsBeGone")).build());
        SubCategoryBuilder telemetrySubcategory = entryBuilder.startSubCategory(Component.translatable("soundsbegone.config.telemetry.used"));
        telemetrySubcategory.add(entryBuilder.startTextDescription(Component.literal("-").append(Component.translatable("soundsbegone.config.telemetry.data.sound"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Component.literal("-").append(Component.translatable("soundsbegone.config.telemetry.data.version.mod"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Component.literal("-").append(Component.translatable("soundsbegone.config.telemetry.data.version.minecraft"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Component.literal("-").append(Component.translatable("soundsbegone.config.telemetry.data.version.java"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Component.literal("-").append(Component.translatable("soundsbegone.config.telemetry.data.loader"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Component.literal("-").append(Component.translatable("soundsbegone.config.telemetry.data.os"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Component.literal("-").append(Component.translatable("soundsbegone.config.telemetry.data.time"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Component.literal("-").append(Component.translatable("soundsbegone.config.telemetry.data.language"))).build());
        settings.addEntry(telemetrySubcategory.build());
        settings.addEntry(entryBuilder.startTextDescription(Component.translatable("soundsbegone.config.telemetry.disclaimer")).build());

        Set<String> recentSounds = Set.copyOf(SoundsBeGoneClient.SoundMap.keySet());
        Set<String> soundIds = new TreeSet<>(recentSounds);
        soundIds.addAll(SoundsBeGoneClient.config.infrequentSounds());
        soundIds.addAll(SoundsBeGoneClient.config.disabledSounds());
        Minecraft.getInstance().getSoundManager().getAvailableSounds()
                .forEach(id -> soundIds.add(id.toString()));

        Map<String, SoundOption> soundEntries = new LinkedHashMap<>();
        for (String soundId : soundIds) {
            SoundState state = SoundsBeGoneClient.config.getSoundState(soundId);
            // Share the entry so a change is visible in every tab that shows this sound.
            SoundOption entry = constructOption(soundId, state);
            soundEntries.put(soundId, entry);
            if (state == SoundState.DISABLED) {
                disabled.addEntry(entry);
            } else {
                if (recentSounds.contains(soundId)) general.addEntry(entry);
                if (state == SoundState.INFREQUENT) infrequent.addEntry(entry);
            }
        }
        AbstractConfigListEntry<?> searchHint = entryBuilder.startTextDescription(
                Component.translatable("soundsbegone.config.search_hint")).build();
        searchHint.setDisplayRequirement(() -> !SoundGroups.hasSearch(searchHint.getConfigScreen()));
        allSounds.addEntry(searchHint);
        SoundGroups.addEntries(allSounds, soundEntries);
        SupportCategory.add(builder, entryBuilder);
        return builder.build();
    }

    private static SoundOption constructOption(String key, SoundState initialState) {
        return new SoundOption(key, initialState, newValue -> {
            if (SoundsBeGoneClient.config.getSoundState(key) == newValue) return;
            switch (newValue) {
                case DISABLED -> {
                    SoundsBeGoneConfig.LOGGER.info("Disabling sound: " + key);
                    SoundsBeGoneClient.config.disableSound(key);
                    SoundsBeGoneClient.telemetry.mutedSound(key);
                }
                case INFREQUENT -> {
                    SoundsBeGoneConfig.LOGGER.info("Setting sound to infrequent: " + key);
                    SoundsBeGoneClient.config.reduceSound(key);
                    SoundsBeGoneClient.telemetry.setInfrequentSound(key, SoundsBeGoneClient.config.getFrequencyPercentage());
                }
                case ENABLED -> {
                    SoundsBeGoneConfig.LOGGER.info("Enabling sound: " + key);
                    SoundsBeGoneClient.config.resetSound(key);
                    SoundsBeGoneClient.telemetry.unmutedSound(key);
                }
            }
        });
    }
}
