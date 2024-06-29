package gg.meza.neoforge;

import gg.meza.SoundsBeGone;
import gg.meza.client.ConfigScreen;
import gg.meza.client.SoundsBeGoneClient;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Date;

@Mod(SoundsBeGone.MOD_ID)
public class SoundsBeGoneForge {

    public SoundsBeGoneForge() {

        SoundsBeGone.LOGGER.info("SoundsBeGone initialized");
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME ,modid = SoundsBeGone.MOD_ID)
    public static class GameModEvents {
        static int tickCounter = 0;

        @SubscribeEvent
        public static void shutdown(GameShuttingDownEvent event) {
            SoundsBeGoneClient.telemetry.flush();
        }

        @SubscribeEvent
        public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
            SoundsBeGoneClient.telemetry.flush();
        }

        @SubscribeEvent
        public static void endTick(ClientTickEvent.Post event) {
            if (SoundsBeGoneClient.openConfig.wasPressed()) {
                MinecraftClient.getInstance().setScreen(ConfigScreen.getConfigeScreen(null));
            }
            if (tickCounter++ < 200) return;
            //remove from SoundMap if the sound has been playing for more than 60 seconds
            SoundsBeGoneClient.SoundMap.entrySet().removeIf(entry -> new Date().getTime() - entry.getValue().getTime() > 60000);
            tickCounter = 0;
        }
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD ,modid = SoundsBeGone.MOD_ID)
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
