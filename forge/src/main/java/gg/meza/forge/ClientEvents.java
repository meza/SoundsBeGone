package gg.meza.forge;

import gg.meza.SoundsBeGone;
import gg.meza.client.ConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.Date;

import static gg.meza.SoundsBeGone.SoundMap;
import static gg.meza.SoundsBeGone.analytics;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    static int tickCounter = 0;

    @SubscribeEvent
    public static void endTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && SoundsBeGone.openConfig.wasPressed()) {
            MinecraftClient.getInstance().setScreen(ConfigScreen.getConfigeScreen(null));
            return;
        }
        if (event.phase != TickEvent.Phase.END && tickCounter++ < 200) return;
        //remove from SoundMap if the sound has been playing for more than 60 seconds
        SoundMap.entrySet().removeIf(entry -> new Date().getTime() - entry.getValue().getTime() > 60000);
        tickCounter = 0;
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        analytics.flush();
    }

    @SubscribeEvent
    public static void shutdown(ServerStoppingEvent event) {
        analytics.flush();
    }
}
