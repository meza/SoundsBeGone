package gg.meza;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import gg.meza.analytics.Analytics;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.SerializationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SoundsBeGoneClient implements ClientModInitializer {

	public static Map<String, Date> SoundMap = new HashMap<String, Date>();
	public static Set<String> DisabledSoundMap = new HashSet<>();
	private int tickCounter = 0;
	public static Analytics analytics = new Analytics();

	@Override
	public void onInitializeClient() {

		Path configPath = FabricLoader.getInstance().getConfigDir().resolve("disabled_sounds.json");
		if (Files.exists(configPath)) {
			try {
				BufferedReader reader = Files.newBufferedReader(configPath);
				Gson gson = new Gson();
				String[] disabledSounds = gson.fromJson(reader, String[].class);
				DisabledSoundMap.addAll(Arrays.asList(disabledSounds));
				reader.close();
			} catch (IOException | JsonParseException e) {
				throw new SerializationException(e);
			}
		}

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (tickCounter++ < 200) return;
			//remove from SoundMap if the sound has been playing for more than 60 seconds
			SoundMap.entrySet().removeIf(entry -> new Date().getTime() - entry.getValue().getTime() > 60000);
			tickCounter = 0;
		});
	}
}
