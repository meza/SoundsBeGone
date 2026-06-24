package gg.meza.soundsbegone.client;

import gg.meza.soundsbegone.SoundsBeGoneConfig;

import gg.meza.supporters.clothconfig.SupportCategory;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Set;

public class ConfigScreen {
    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("soundsbegone.config.title"))
                .setSavingRunnable(() -> {
                    SoundsBeGoneClient.config.saveConfig();
                });
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("soundsbegone.config.category.latest"));
        ConfigCategory configured = builder.getOrCreateCategory(Component.translatable("soundsbegone.config.category.configured"));
        ConfigCategory settings = builder.getOrCreateCategory(Component.translatable("soundsbegone.config.category.settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        settings.addEntry(
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

        SoundsBeGoneClient.SoundMap
                .keySet()
                .stream()
                .filter(s -> SoundsBeGoneClient.config.getSoundVolume(s) == 100 && !SoundsBeGoneClient.config.isSoundInfrequent(s))
                .sorted()
                .forEach((key) -> general.addEntry(constructOption(entryBuilder, key)));

        Set<String> allModifiedSounds = new HashSet<>();
        allModifiedSounds.addAll(SoundsBeGoneClient.config.disabledSounds());
        allModifiedSounds.addAll(SoundsBeGoneClient.config.reducedSounds());
        allModifiedSounds.addAll(SoundsBeGoneClient.config.infrequentSounds());

        allModifiedSounds.stream()
                .sorted()
                .forEach((key) -> configured.addEntry(constructOption(entryBuilder, key)));

        SupportCategory.add(builder, entryBuilder);
        return builder.build();
    }

    private static AbstractConfigListEntry<?> constructOption(ConfigEntryBuilder builder, String key) {
        int currentVolume = SoundsBeGoneClient.config.getSoundVolume(key);
        boolean isInfrequent = SoundsBeGoneClient.config.isSoundInfrequent(key);

        SubCategoryBuilder sub = builder.startSubCategory(Component.translatable(key));
        sub.setExpanded(false);

        sub.add(builder.startIntSlider(
                        Component.translatable("soundsbegone.config.sound.volume"),
                        currentVolume, 0, 100)
                .setDefaultValue(100)
                .setSaveConsumer(newVolume -> {
                    int oldVolume = SoundsBeGoneClient.config.getSoundVolume(key);
                    if (oldVolume == 0 && newVolume > 0) {
                        SoundsBeGoneClient.telemetry.unmutedSound(key);
                    } else if (oldVolume > 0 && newVolume == 0) {
                        SoundsBeGoneClient.telemetry.mutedSound(key);
                    }
                    SoundsBeGoneClient.config.setSoundVolume(key, newVolume);
                })
                .setTextGetter(value -> Component.literal(value + "%"))
                .build()
        );

        sub.add(builder.startBooleanToggle(
                        Component.translatable("soundsbegone.config.sound.infrequent"),
                        isInfrequent)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> {
                    boolean wasInfrequent = SoundsBeGoneClient.config.isSoundInfrequent(key);
                    SoundsBeGoneClient.config.setInfrequent(key, newValue);
                    if (!wasInfrequent && newValue) {
                        SoundsBeGoneConfig.LOGGER.info("Setting sound to infrequent: {}", key);
                        SoundsBeGoneClient.telemetry.setInfrequentSound(key, SoundsBeGoneClient.config.getFrequencyPercentage());
                    } else if (wasInfrequent && !newValue) {
                        SoundsBeGoneConfig.LOGGER.info("Removing sound from infrequent: {}", key);
                    }
                })
                .build()
        );

        return sub.build();
    }
}