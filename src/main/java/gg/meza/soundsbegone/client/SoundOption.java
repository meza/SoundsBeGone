package gg.meza.soundsbegone.client;

import gg.meza.soundsbegone.SoundState;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.TabbedConfigScreen;
import net.minecraft.client.Minecraft;
//? if >= 26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

final class SoundOption extends AbstractConfigListEntry<SoundState> {
    private final SoundState initialState;
    private final CycleButton<SoundState> stateButton;
    private final Button resetButton;
    private Component searchOnlyTab;

    SoundOption(String soundId, SoundState initialState, Consumer<SoundState> save) {
        super(Component.translatable(soundId), false);
        this.initialState = initialState;
        this.saveCallback = save;
        stateButton = CycleButton.builder(
                        (SoundState state) -> Component.translatable(
                                "soundsbegone.config.sound.state." + state.name().toLowerCase(Locale.ROOT)), initialState)
                .withValues(SoundState.values())
                .displayOnlyValue()
                .create(0, 0, 100, 20, getFieldName());
        Component reset = Component.translatable("text.cloth-config.reset_value");
        resetButton = Button.builder(reset, button -> setValue(initialState))
                .size(Minecraft.getInstance().font.width(reset) + 8, 20).build();
        appendSearchTags(List.of(soundId));
        appendSearchTags(Arrays.asList(soundId.split("[.:/_-]+")));
    }

    void showOnlyWithSearchIn(Component tab) {
        searchOnlyTab = tab;
    }

    @Override
    public boolean isDisplayed() {
        return super.isDisplayed() && (searchOnlyTab == null
                || !(getConfigScreen() instanceof TabbedConfigScreen screen)
                || !searchOnlyTab.equals(screen.getSelectedCategory())
                || SoundGroups.hasSearch(screen));
    }

    void setValue(SoundState state) {
        stateButton.setValue(state);
    }

    @Override
    public SoundState getValue() {
        return stateButton.getValue();
    }

    @Override
    public Optional<SoundState> getDefaultValue() {
        return Optional.of(initialState);
    }

    @Override
    public boolean isEdited() {
        return super.isEdited() || getValue() != initialState;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(stateButton, resetButton);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(stateButton, resetButton);
    }

    @Override
    //? if >= 26.1 {
    public void extractRenderState(GuiGraphicsExtractor graphics, int index, int y, int x, int entryWidth,
                                   int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
        super.extractRenderState(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);
    //?} else {
    /*public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth,
                       int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);
    *///?}
        int controlsWidth = Math.min(150, entryWidth / 2);
        resetButton.setX(x + entryWidth - resetButton.getWidth());
        resetButton.setY(y);
        resetButton.active = isEditable() && getValue() != initialState;
        stateButton.setX(x + entryWidth - controlsWidth);
        stateButton.setY(y);
        stateButton.setWidth(Math.max(1, controlsWidth - resetButton.getWidth() - 2));
        stateButton.active = isEditable();
        var font = Minecraft.getInstance().font;
        Component label = SoundGroups.fitLabel(getDisplayedFieldName(), entryWidth - controlsWidth - 4);
        //? if >= 26.1 {
        graphics.text(font, label.getVisualOrderText(), x, y + 6, getPreferredTextColor());
        stateButton.extractRenderState(graphics, mouseX, mouseY, delta);
        resetButton.extractRenderState(graphics, mouseX, mouseY, delta);
        //?} else {
        /*graphics.drawString(font, label.getVisualOrderText(), x, y + 6, getPreferredTextColor());
        stateButton.render(graphics, mouseX, mouseY, delta);
        resetButton.render(graphics, mouseX, mouseY, delta);
        *///?}
    }
}
