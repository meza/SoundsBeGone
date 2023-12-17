package gg.meza.neoforge;

import gg.meza.SoundsBeGone;
import gg.meza.client.ConfigScreen;
import gg.meza.client.SoundsBeGoneClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

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
