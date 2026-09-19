package gg.meza.soundsbegone.client;

import gg.meza.soundsbegone.SoundState;
import gg.meza.soundsbegone.SoundsBeGoneConfig;

import gg.meza.supporters.clothconfig.SupportCategory;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen {
    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("soundsbegone.config.title"))
                .setSavingRunnable(() -> {
                    SoundsBeGoneClient.config.saveConfig();
                });
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("soundsbegone.config.category.latest"));
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

        SoundsBeGoneClient.SoundMap
                .keySet()
                .stream()
                .filter(s -> !SoundsBeGoneClient.config.isSoundDisabled(s))
                .sorted()
                .forEach((key) -> general.addEntry(constructOption(entryBuilder, key)));

        SoundsBeGoneClient.config.infrequentSounds().stream().sorted().forEach((key) -> infrequent.addEntry(constructOption(entryBuilder, key)));
        SoundsBeGoneClient.config.disabledSounds().stream().sorted().forEach((key) -> disabled.addEntry(constructOption(entryBuilder, key)));
        SupportCategory.add(builder, entryBuilder);
        return builder.build();
    }

    private static AbstractConfigListEntry<?> constructOption(ConfigEntryBuilder builder, String key) {
        return builder
                .startEnumSelector(Component.translatable(key), SoundState.class, SoundsBeGoneClient.config.getSoundState(key))
                .setDefaultValue(SoundsBeGoneClient.config.getSoundState(key))
                .setEnumNameProvider((state) -> Component.translatable("soundsbegone.config.sound.state." + state.name().toLowerCase()))
                .setSaveConsumer(newValue -> {
                    boolean hasChanged = SoundsBeGoneClient.config.getSoundState(key) != newValue;
                    if (!hasChanged) return;
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
                })
                .build();
    }
}
