package gg.meza;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import gg.meza.config.SaveHandler;
import net.minecraft.text.Text;

import static gg.meza.SoundsBeGoneClient.DisabledSoundMap;
import static gg.meza.SoundsBeGoneClient.SoundMap;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {

        return parent -> {
            ConfigCategory.Builder generalCategory = ConfigCategory.createBuilder()
                    .name(Text.of("Sounds Be Gone Config"));

            OptionGroup.Builder latestOption = OptionGroup.createBuilder()
                    .name(Text.of("Sounds played in the last 60 seconds"));

            OptionGroup.Builder disabledOption = OptionGroup.createBuilder()
                    .name(Text.of("Disabled sounds"));


            SoundMap
                    .keySet()
                    .stream()
                    .filter(s -> !SoundsBeGoneClient.DisabledSoundMap.contains(s))
                    .sorted()
                    .forEach((key) -> {
                        latestOption.option(
                                Option.createBuilder(Boolean.class)
                                        .name(Text.of(key))
                                        .binding(false, () -> DisabledSoundMap.contains(key), (value) -> {
                                            if (value) {
                                                DisabledSoundMap.add(key);
                                            } else {
                                                DisabledSoundMap.remove(key);
                                            }
                                        })
                                        .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                                        .build()
                        );
                    });

            DisabledSoundMap.stream().sorted().forEach((key) -> {
                disabledOption.option(
                        Option.createBuilder(Boolean.class)
                                .name(Text.of(key))
                                .binding(true, () -> DisabledSoundMap.contains(key), (value) -> {
                                    if (value) {
                                        DisabledSoundMap.add(key);
                                    } else {
                                        DisabledSoundMap.remove(key);
                                    }
                                })
                                .controller(opt -> BooleanControllerBuilder.create(opt).coloured(true))
                                .build()
                );
            });

            if(!SoundMap.isEmpty()) {
                generalCategory.group(latestOption.build());
            }

            if(!DisabledSoundMap.isEmpty()) {
                generalCategory.group(disabledOption.build());
            }

            return YetAnotherConfigLib.createBuilder()
                    .title(Text.of("Sounds Be Gone Config"))
                    .category(generalCategory.build())
                    .save(SaveHandler::save)
                    .build()
                    .generateScreen(parent);
        };

//
//        return parent -> {
//            // Return the screen here with the one you created from Cloth Config Builder
//            ConfigBuilder builder = ConfigBuilder.create()
//                    .setParentScreen(parent)
//                    .setTitle(Text.of("Sounds Be Gone Config"))
//                    .setSavingRunnable(() -> {
//                        try {
//                            // Save config
//                            Gson gson = new Gson();
//                            Path configPath = FabricLoader.getInstance().getConfigDir().resolve("disabled_sounds.json");
//                            BufferedWriter writer = Files.newBufferedWriter(configPath);
//                            gson.toJson(SoundsBeGoneClient.DisabledSoundMap, writer);
//                            writer.close();
//                        } catch (IOException e) {
//                            throw new SerializationException(e);
//                        }
//
//                    });
//            ConfigCategory general = builder.getOrCreateCategory(Text.of("Played in the last 60 seconds"));
//            ConfigCategory disabled = builder.getOrCreateCategory(Text.of("Disabled sounds"));
//            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
//
//            SoundMap
//                    .keySet()
//                    .stream()
//                    .filter(s -> !SoundsBeGoneClient.DisabledSoundMap.contains(s))
//                    .sorted()
//                    .forEach((key) -> general.addEntry(constructOption(entryBuilder, key)));
//
//            DisabledSoundMap.stream().sorted().forEach((key) -> disabled.addEntry(constructOption(entryBuilder, key)));
//
//            return builder.build();
//        };
    }

//    private AbstractConfigListEntry constructOption(ConfigEntryBuilder builder, String key) {
//        return builder
//                .startBooleanToggle(Text.of(key), SoundsBeGoneClient.DisabledSoundMap.contains(key))
//                .setDefaultValue(SoundsBeGoneClient.DisabledSoundMap.contains(key))
//                .setSaveConsumer(newValue -> {
//                    if (newValue) {
//                        SoundsBeGoneClient.DisabledSoundMap.add(key);
//                    } else {
//                        SoundsBeGoneClient.DisabledSoundMap.remove(key);
//                    }
//                })
//                .setYesNoTextSupplier(bool -> bool ? Text.of("Enable") : Text.of("Disable"))
//                .build();
//    }
}
