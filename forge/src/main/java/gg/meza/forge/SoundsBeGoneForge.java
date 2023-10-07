package gg.meza.forge;

import gg.meza.SoundsBeGone;
import gg.meza.client.ConfigScreen;
import gg.meza.client.SoundsBeGoneClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(SoundsBeGone.MOD_ID)
public class SoundsBeGoneForge {

    public SoundsBeGoneForge() {

        SoundsBeGone.LOGGER.info("SoundsBeGone initialized");
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> ConfigScreen.getConfigeScreen(screen)));

    }

    @Mod.EventBusSubscriber(modid = SoundsBeGone.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            SoundsBeGoneClient.initClient();
        }

        @SubscribeEvent
        public static void keyBinds(RegisterKeyMappingsEvent event) {
            event.register(SoundsBeGoneClient.openConfig);
        }
    }
}
