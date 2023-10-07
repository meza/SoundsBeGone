package gg.meza.forge;

import gg.meza.client.ConfigScreen;
import gg.meza.client.SoundsBeGoneClient;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Date;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    static int tickCounter = 0;

    @SubscribeEvent
    public static void endTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && SoundsBeGoneClient.openConfig.wasPressed()) {
            MinecraftClient.getInstance().setScreen(ConfigScreen.getConfigeScreen(null));
        }
        if (event.phase != TickEvent.Phase.END && tickCounter++ < 200) return;
        //remove from SoundMap if the sound has been playing for more than 60 seconds
        SoundsBeGoneClient.SoundMap.entrySet().removeIf(entry -> new Date().getTime() - entry.getValue().getTime() > 60000);
        tickCounter = 0;
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        SoundsBeGoneClient.analytics.flush();
    }

    @SubscribeEvent
    public static void shutdown(ServerStoppingEvent event) {
        SoundsBeGoneClient.analytics.flush();
    }
}
