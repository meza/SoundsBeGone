package gg.meza.soundsbegone.client;

import gg.meza.soundsbegone.SoundState;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import net.minecraft.client.Minecraft;
//? if >= 26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

final class SoundGroupDropdown extends SubCategoryListEntry {
    private final List<SoundOption> sounds;
    private final Button enableAll;
    private final Button disableAll;
    private int labelWidth = Integer.MAX_VALUE;

    SoundGroupDropdown(Component title, List<SoundOption> sounds) {
        super(title, new ArrayList<AbstractConfigListEntry>(sounds), false);
        this.sounds = List.copyOf(sounds);
        enableAll = Button.builder(Component.translatable("soundsbegone.config.group.enable_all"),
                button -> setAll(SoundState.ENABLED)).build();
        disableAll = Button.builder(Component.translatable("soundsbegone.config.group.disable_all"),
                button -> setAll(SoundState.DISABLED)).build();
    }

    private void setAll(SoundState state) {
        sounds.forEach(sound -> sound.setValue(state));
    }

    @Override
    public boolean isDisplayed() {
        return super.isDisplayed() && SoundGroups.hasSearch(getConfigScreen());
    }

    @Override
    public Component getDisplayedFieldName() {
        return SoundGroups.fitLabel(super.getDisplayedFieldName(), labelWidth);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return Stream.concat(Stream.of(enableAll, disableAll), super.children().stream()).toList();
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return Stream.concat(super.narratables().stream(), Stream.of(enableAll, disableAll)).toList();
    }

    private void positionButtons(int x, int y, int entryWidth) {
        var font = Minecraft.getInstance().font;
        int maxButtonWidth = entryWidth / 3;
        enableAll.setWidth(Math.min(maxButtonWidth, font.width(enableAll.getMessage()) + 10));
        disableAll.setWidth(Math.min(maxButtonWidth, font.width(disableAll.getMessage()) + 10));
        labelWidth = entryWidth - enableAll.getWidth() - disableAll.getWidth() - 10;
        disableAll.setX(x + entryWidth - disableAll.getWidth());
        disableAll.setY(y);
        enableAll.setX(disableAll.getX() - enableAll.getWidth() - 3);
        enableAll.setY(y);
        enableAll.active = isEditable() && sounds.stream().anyMatch(sound -> sound.getValue() != SoundState.ENABLED);
        disableAll.active = isEditable() && sounds.stream().anyMatch(sound -> sound.getValue() != SoundState.DISABLED);
    }

    @Override
    //? if >= 26.1 {
    public void extractRenderState(GuiGraphicsExtractor graphics, int index, int y, int x, int entryWidth,
                                   int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
        positionButtons(x, y, entryWidth);
        super.extractRenderState(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);
        enableAll.extractRenderState(graphics, mouseX, mouseY, delta);
        disableAll.extractRenderState(graphics, mouseX, mouseY, delta);
    }
    //?} else {
    /*public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth,
                       int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
        positionButtons(x, y, entryWidth);
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);
        enableAll.render(graphics, mouseX, mouseY, delta);
        disableAll.render(graphics, mouseX, mouseY, delta);
    }
    *///?}
}
