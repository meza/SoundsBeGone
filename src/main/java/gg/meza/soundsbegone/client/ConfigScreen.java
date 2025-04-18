package gg.meza.soundsbegone.client;

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
        ConfigCategory settings = builder.getOrCreateCategory(Text.translatable("soundsbegone.config.category.settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

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
        settings.addEntry(telemetrySubcategory.build());
        settings.addEntry(entryBuilder.startTextDescription(Text.translatable("soundsbegone.config.telemetry.disclaimer")).build());

        SoundsBeGoneClient.SoundMap
                .keySet()
                .stream()
                .filter(s -> !SoundsBeGoneClient.config.isSoundDisabled(s))
                .sorted()
                .forEach((key) -> general.addEntry(constructOption(entryBuilder, key)));

        SoundsBeGoneClient.config.disabledSounds().stream().sorted().forEach((key) -> disabled.addEntry(constructOption(entryBuilder, key)));
        SupportCategory.add(builder, entryBuilder);
        return builder.build();
    }

    private static AbstractConfigListEntry<?> constructOption(ConfigEntryBuilder builder, String key) {
        return builder
                .startBooleanToggle(Text.translatable(key), SoundsBeGoneClient.config.isSoundDisabled(key))
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
                .setYesNoTextSupplier(bool -> bool ? Text.translatable("soundsbegone.enable") : Text.translatable("soundsbegone.disable"))
                .build();
    }
}
