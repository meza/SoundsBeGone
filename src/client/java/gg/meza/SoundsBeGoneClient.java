package gg.meza;

import gg.meza.config.SaveHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.io.IOException;
import java.util.*;

public class SoundsBeGoneClient implements ClientModInitializer {

	public static Map<String, Date> SoundMap = new HashMap<String, Date>();
	public static Set<String> DisabledSoundMap = new HashSet<>();
	private int tickCounter = 0;

	@Override
	public void onInitializeClient() {

		try {
			SaveHandler.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (tickCounter++ < 200) return;
			//remove from SoundMap if the sound has been playing for more than 60 seconds
			SoundMap.entrySet().removeIf(entry -> new Date().getTime() - entry.getValue().getTime() > 60000);
			tickCounter = 0;
		});
	}
}
