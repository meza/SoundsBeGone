package gg.meza.soundsbegone.client;

import gg.meza.soundsbegone.SoundState;
import gg.meza.soundsbegone.SoundsBeGoneConfig;

import gg.meza.supporters.clothconfig.SupportCategory;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("soundsbegone.config.title"))
                .setSavingRunnable(() -> {
                    SoundsBeGoneClient.config.saveConfig();
                });
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("soundsbegone.config.category.latest"));
        ConfigCategory disabled = builder.getOrCreateCategory(Text.translatable("soundsbegone.config.category.disabled"));
        ConfigCategory infrequent = builder.getOrCreateCategory(Text.translatable("soundsbegone.config.category.infrequent"));
        ConfigCategory settings = builder.getOrCreateCategory(Text.translatable("soundsbegone.config.category.settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        infrequent.addEntry(
                entryBuilder.startDoubleField(Text.translatable("soundsbegone.config.infrequent.frequency.title"), SoundsBeGoneClient.config.getFrequencyPercentage())
                        .setMin(0.001)
                        .setMax(99.999)
                        .setTooltip(Text.translatable("soundsbegone.config.infrequent.frequency.tooltip"))
                        .setSaveConsumer(newValue -> {
                            SoundsBeGoneClient.config.setFrequencyPercentage(newValue);
                        })
                        .setDefaultValue(SoundsBeGoneClient.config.getFrequencyPercentage())
                        .build()
        );

        settings.addEntry(entryBuilder.startBooleanToggle(Text.translatable("soundsbegone.config.telemetry.switch"), SoundsBeGoneClient.config.isTelemetryEnabled())
                .setDefaultValue(SoundsBeGoneClient.config.isTelemetryEnabled())
                .setSaveConsumer(newValue -> {
                    SoundsBeGoneClient.config.toggleTelemetry(newValue);
                })
                .build());

        settings.addEntry(entryBuilder.startTextDescription(Text.translatable("soundsbegone.config.telemetry.description")).build());
        settings.addEntry(entryBuilder.startTextDescription(Text.translatable("soundsbegone.config.telemetry.verify", "https://github.com/meza/SoundsBeGone")).build());
        SubCategoryBuilder telemetrySubcategory = entryBuilder.startSubCategory(Text.translatable("soundsbegone.config.telemetry.used"));
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.literal("-").append(Text.translatable("soundsbegone.config.telemetry.data.sound"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.literal("-").append(Text.translatable("soundsbegone.config.telemetry.data.version.mod"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.literal("-").append(Text.translatable("soundsbegone.config.telemetry.data.version.minecraft"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.literal("-").append(Text.translatable("soundsbegone.config.telemetry.data.version.java"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.literal("-").append(Text.translatable("soundsbegone.config.telemetry.data.loader"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.literal("-").append(Text.translatable("soundsbegone.config.telemetry.data.os"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.literal("-").append(Text.translatable("soundsbegone.config.telemetry.data.time"))).build());
        telemetrySubcategory.add(entryBuilder.startTextDescription(Text.literal("-").append(Text.translatable("soundsbegone.config.telemetry.data.language"))).build());
        settings.addEntry(telemetrySubcategory.build());
        settings.addEntry(entryBuilder.startTextDescription(Text.translatable("soundsbegone.config.telemetry.disclaimer")).build());

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
                .startEnumSelector(Text.translatable(key), SoundState.class, SoundsBeGoneClient.config.getSoundState(key))
                .setDefaultValue(SoundsBeGoneClient.config.getSoundState(key))
                .setEnumNameProvider((state) -> Text.translatable("soundsbegone.config.sound.state." + state.name().toLowerCase()))
                .setSaveConsumer(newValue -> {
                    SoundsBeGoneClient.config.setSoundState(key, newValue);
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
                            SoundsBeGoneClient.config.enableSound(key);
                            SoundsBeGoneClient.telemetry.unmutedSound(key);
                        }
                    }
                })
                .build();
    }
}
