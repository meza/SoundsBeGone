package gg.meza.soundsbegone.telemetry;

import com.posthog.java.PostHog;
import gg.meza.soundsbegone.SoundsBeGoneConfig;
import gg.meza.soundsbegone.client.SoundsBeGoneClient;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Telemetry {
    private static final String POSTHOG_API_KEY = "POSTHOG_API_KEY_REPL";
    private static final String POSTHOG_HOST = "https://eu.posthog.com";
    private final PostHog posthog;
    private final Map<String, Integer> blockedSoundsCount = new ConcurrentHashMap<>();

    // Not actually sending any user info, just using the uuid to create a new uuid that cannot be traced back to the user
    private final String uuid = DigestUtils.sha256Hex(MinecraftClient.getInstance().getSession().getUsername());
    private final String OS_NAME = System.getProperty("os.name");
    /*? if 1.21.6*/
    private final String MC_VERSION = "1.21.6";
    /*? if 1.21.5*/
    /*private final String MC_VERSION = "1.21.5";*/
    /*? if 1.21.4*/
    /*private final String MC_VERSION = "1.21.4";*/
    /*? if 1.21*/
    /*private final String MC_VERSION = "1.21";*/
    /*? if 1.19.4*/
    /*private final String MC_VERSION = "1.19.4";*/
    private final String JAVA_VERSION = System.getProperty("java.version");

    /*? if fabric*/
    private final String LOADER = "fabric";
    /*? if forge*/
    /*private final String LOADER = "forge";*/
    /*? if neoforge*/
    /*private final String LOADER = "neoforge";*/

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final MinecraftClient client;

    public Telemetry(MinecraftClient client) {
        this.client = client;
        this.posthog = new PostHog.Builder(POSTHOG_API_KEY).host(POSTHOG_HOST).build();
        if (SoundsBeGoneClient.config.isTelemetryEnabled()) {
            scheduler.scheduleAtFixedRate(this::sendBlockedData, 30, 30, TimeUnit.MINUTES);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }
    private void sendEvent(String event, String sound) {
        this.sendEvent(event, sound, Map.of());
    }

    private void sendEvent(String event, String sound, Map<String, Object> extraProps) {
        if (!SoundsBeGoneClient.config.isTelemetryEnabled()) {
            return;
        }
        String languageCode = client.getLanguageManager().getLanguage().toLowerCase();
        Map<String, Object> baseProps = new ConcurrentHashMap<>(Map.of(
                "sound", sound,
                "Minecraft Version", MC_VERSION,
                "OS", OS_NAME,
                "Local Time", new java.util.Date().toString(),
                "ModVersion", SoundsBeGoneConfig.VERSION,
                "Java Version", JAVA_VERSION,
                "Loader", LOADER,
                "Language", languageCode
        ));

        baseProps.putAll(extraProps);

        this.posthog.capture(this.uuid, event, baseProps);
    }

    public void mutedSound(String sound) {
        this.sendEvent("Muted Sound", sound);
    }

    public void blockedSound(String sound) {
        if (this.blockedSoundsCount.containsKey(sound)) {
            this.blockedSoundsCount.put(sound, this.blockedSoundsCount.get(sound) + 1);
        } else {
            this.blockedSoundsCount.put(sound, 1);
        }

        if(this.blockedSoundsCount.get(sound) > Integer.MAX_VALUE - 1000) {
            this.sendEvent("Blocked Sound", sound, Map.of(
                    "count", this.blockedSoundsCount.get(sound)
            ));
            this.blockedSoundsCount.remove(sound);
        }
    }

    public void unmutedSound(String sound) {
        this.sendEvent("UnMuted Sound", sound);
    }

    public void sendBlockedData() {
        Map<String, Integer> soundsToSend = new HashMap<>(this.blockedSoundsCount);
        this.blockedSoundsCount.clear();

        soundsToSend.forEach((sound, count) -> {
            this.sendEvent("Blocked Sound", sound, Map.of(
                    "count", count
            ));
        });
    }

    public void flush() {
        SoundsBeGoneConfig.LOGGER.debug("Flushing telemetry");
        sendBlockedData();
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void startMinecraft() {
        this.sendEvent("Started Minecraft", "");
    }
}
