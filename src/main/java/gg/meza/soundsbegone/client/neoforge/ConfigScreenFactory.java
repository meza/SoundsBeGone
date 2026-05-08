/*? if neoforge {*/
/*package gg.meza.soundsbegone.client.neoforge;

import gg.meza.soundsbegone.client.ConfigScreen;
/^? if > 1.21 {^/
import net.neoforged.fml.ModContainer;
/^?}^/
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ConfigScreenFactory implements IConfigScreenFactory {

    /^? if > 1.21 {^/
    @Override
    public Screen createScreen(ModContainer modContainer, Screen parent) {
        return ConfigScreen.getConfigScreen(parent);
    }
    /^?} else {^/
    /^@Override
    public Screen createScreen(Minecraft arg, Screen parent) {
        return ConfigScreen.getConfigScreen(parent);
    }
    ^//^?}^/
}
*//*?}*/
