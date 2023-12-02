package gg.meza.fabric;


import gg.meza.client.ConfigScreen;
import gg.meza.client.SoundsBeGoneClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class SoundsBeGoneFabric implements ClientModInitializer {

	private int tickCounter = 0;

	@Override
	public void onInitializeClient() {

		SoundsBeGoneClient.initClient();

		KeyBindingHelper.registerKeyBinding(SoundsBeGoneClient.openConfig);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (SoundsBeGoneClient.openConfig.wasPressed()) {
				client.setScreen(ConfigScreen.getConfigeScreen(null));
			}
			if (tickCounter++ < 200) return;
			//remove from SoundMap if the sound has been playing for more than 60 seconds
			SoundsBeGoneClient.SoundMap.entrySet().removeIf(entry -> new Date().getTime() - entry.getValue().getTime() > 60000);
			tickCounter = 0;
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			SoundsBeGoneClient.analytics.flush();
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			SoundsBeGoneClient.analytics.flush();
		});
	}



}
