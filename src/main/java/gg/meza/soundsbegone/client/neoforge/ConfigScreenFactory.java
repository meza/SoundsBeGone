/*? if neoforge {*/
package gg.meza.soundsbegone.client.neoforge;

import gg.meza.soundsbegone.client.ConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
/*? if > 1.21 {*/
/*import net.neoforged.fml.ModContainer;
*//*?}*/
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ConfigScreenFactory implements IConfigScreenFactory {

    /*? if > 1.21 {*/
    /*@Override
    public Screen createScreen(ModContainer modContainer, Screen parent) {
        return ConfigScreen.getConfigScreen(parent);
    }
    *//*?} else {*/
    @Override
    public Screen createScreen(MinecraftClient arg, Screen parent) {
        return ConfigScreen.getConfigScreen(parent);
    }
    /*?}*/
}
/*?}*/
