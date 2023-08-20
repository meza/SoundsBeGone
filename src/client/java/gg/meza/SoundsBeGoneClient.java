package gg.meza;


import gg.meza.analytics.Analytics;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.world.WorldEvents;

import java.util.*;

public class SoundsBeGoneClient implements ClientModInitializer {

	public static Map<String, Date> SoundMap = new HashMap<String, Date>();
	private int tickCounter = 0;
	public static Config config = new Config();
	public static Analytics analytics;

	@Override
	public void onInitializeClient() {

		config.initConfig();
		analytics = new Analytics();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (tickCounter++ < 200) return;
			//remove from SoundMap if the sound has been playing for more than 60 seconds
			SoundMap.entrySet().removeIf(entry -> new Date().getTime() - entry.getValue().getTime() > 60000);
			tickCounter = 0;
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			analytics.flush();
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			analytics.flush();
		});
	}



}
