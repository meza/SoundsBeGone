package gg.meza.soundsbegone.client;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

final class SoundGroups {
    private SoundGroups() {}

    static boolean hasSearch(ConfigScreen screen) {
        // A blank tag only matches when Cloth Config has no search terms.
        return screen != null && !screen.matchesSearch(List.of("").iterator());
    }

    static Component fitLabel(Component label, int width) {
        var font = Minecraft.getInstance().font;
        if (font.width(label) <= width) return label;
        String shortened = font.plainSubstrByWidth(label.getString(), Math.max(0, width - font.width("…")));
        return Component.literal(shortened + "…").withStyle(label.getStyle());
    }

    static void addEntries(ConfigCategory category, Map<String, SoundOption> soundEntries) {
        Map<String, Group> groups = new TreeMap<>();
        Map<String, Group> groupsBySound = new HashMap<>();
        soundEntries.forEach((soundId, entry) -> {
            String[] parts = soundId.split("[:.]", 4);
            if (parts.length == 4 && (parts[1].equals("entity") || parts[1].equals("block"))) {
                Group group = getGroup(groups, parts[0], parts[1], parts[2]);
                group.entries.add(entry);
                groupsBySound.put(soundId, group);
            }
        });

        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
            SoundType type = block.defaultBlockState().getSoundType();
            // Blocks such as oak planks share sound events with other blocks.
            for (SoundEvent sound : List.of(type.getBreakSound(), type.getStepSound(),
                    type.getPlaceSound(), type.getHitSound(), type.getFallSound())) {
                String soundId = sound.location().toString();
                SoundOption entry = soundEntries.get(soundId);
                if (entry == null) continue;
                Group group = groupsBySound.computeIfAbsent(soundId, ignored -> {
                    Group blockGroup = getGroup(groups, blockId.getNamespace(), "block", blockId.getPath());
                    blockGroup.entries.add(entry);
                    return blockGroup;
                });
                group.aliases.add(blockId.toString());
                group.aliases.add(block.getName().getString());
            }
        }

        for (Group group : groups.values()) {
            List<String> searchTags = new ArrayList<>();
            for (String alias : group.aliases) {
                searchTags.add(alias);
                searchTags.addAll(Arrays.asList(alias.split("[\\s.:/_-]+")));
            }
            for (SoundOption entry : group.entries) {
                // Cloth Config filters the expanded rows as well as their heading.
                entry.appendSearchTags(searchTags);
            }
            category.addEntry(new SoundGroupDropdown(group.title, group.entries));
        }
        soundEntries.forEach((soundId, entry) -> {
            if (!groupsBySound.containsKey(soundId)) {
                entry.showOnlyWithSearchIn(category.getCategoryKey());
                category.addEntry(entry);
            }
        });
    }

    private static Group getGroup(Map<String, Group> groups, String namespace, String kind, String name) {
        String groupId = namespace + ":" + kind + "." + name;
        return groups.computeIfAbsent(groupId, ignored -> {
            String translationKey = kind + "." + namespace + "." + name;
            String fallback = name.replace('_', ' ');
            if (!namespace.equals("minecraft")) fallback = namespace + ": " + fallback;
            Component label = Component.translatableWithFallback(translationKey, fallback);
            Group group = new Group(Component.translatable("soundsbegone.config.group." + kind, label));
            group.aliases.add(groupId);
            group.aliases.add(label.getString());
            return group;
        });
    }

    private static final class Group {
        private final Component title;
        private final List<SoundOption> entries = new ArrayList<>();
        private final Set<String> aliases = new LinkedHashSet<>();

        private Group(Component title) {
            this.title = title;
        }
    }
}
