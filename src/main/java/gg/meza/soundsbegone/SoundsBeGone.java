package gg.meza.soundsbegone;

/*? if fabric {*/
import gg.meza.soundsbegone.client.ConfigScreen;
import gg.meza.soundsbegone.client.SoundsBeGoneClient;
import gg.meza.soundsbegone.client.TranslationReminder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.*;

public class SoundsBeGone implements ClientModInitializer {

    private int tickCounter = 0;

    @Override
    public void onInitializeClient() {

        SoundsBeGoneClient.initClient();

        KeyBindingHelper.registerKeyBinding(SoundsBeGoneClient.openConfig);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (SoundsBeGoneClient.openConfig.wasPressed()) {
                client.setScreen(ConfigScreen.getConfigScreen(null));
            }
            if (tickCounter++ < 200) return;
            //remove from SoundMap if the sound has been playing for more than 60 seconds
            SoundsBeGoneClient.SoundMap.entrySet().removeIf(entry -> new Date().getTime() - entry.getValue().getTime() > 60000);
            tickCounter = 0;
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            TranslationReminder.notify(client);
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            SoundsBeGoneClient.telemetry.startMinecraft();
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            SoundsBeGoneClient.telemetry.flush();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            SoundsBeGoneClient.telemetry.flush();
        });
    }
}
/*?}*/

/*? if neoforge {*/
/*import gg.meza.soundsbegone.client.ConfigScreen;
import gg.meza.soundsbegone.client.SoundsBeGoneClient;
import gg.meza.soundsbegone.client.TranslationReminder;

import gg.meza.soundsbegone.client.neoforge.ConfigScreenFactory;
import net.minecraft.client.MinecraftClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Date;

@Mod(SoundsBeGoneConfig.MOD_ID)
public class SoundsBeGone {

    public SoundsBeGone(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, new ConfigScreenFactory());

        SoundsBeGoneConfig.LOGGER.info("SoundsBeGone initialized");
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME ,modid = SoundsBeGoneConfig.MOD_ID)
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
                MinecraftClient.getInstance().setScreen(ConfigScreen.getConfigScreen(null));
            }
            if (tickCounter++ < 200) return;
            //remove from SoundMap if the sound has been playing for more than 60 seconds
            SoundsBeGoneClient.SoundMap.entrySet().removeIf(entry -> new Date().getTime() - entry.getValue().getTime() > 60000);
            tickCounter = 0;
        }

        @SubscribeEvent
        public static void clientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
            TranslationReminder.notify(MinecraftClient.getInstance());
        }
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD ,modid = SoundsBeGoneConfig.MOD_ID)
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

*//*?}*/
