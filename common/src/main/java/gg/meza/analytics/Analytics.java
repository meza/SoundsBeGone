package gg.meza.analytics;

import com.posthog.java.PostHog;
import gg.meza.SoundsBeGone;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;

public class Analytics {
    private static final String POSTHOG_API_KEY = "POSTHOG_API_KEY_REPL";
    private static final String POSTHOG_HOST = "https://eu.posthog.com";
    private final PostHog posthog;
    private final Map<String, Integer> blockedSoundsCount = new HashMap<>();

    // Not actually sending any user info, just using the uuid to create a new uuid that cannot be traced back to the user
    private final String uuid = DigestUtils.sha256Hex(MinecraftClient.getInstance().getSession().getUsername());
    private final String OS_NAME = System.getProperty("os.name");
    private final String MC_VERSION = MinecraftClient.getInstance().getGameVersion();
    private final String JAVA_VERSION = System.getProperty("java.version");

    public Analytics() {
        this.posthog = new PostHog.Builder(POSTHOG_API_KEY).host(POSTHOG_HOST).build();
        if (SoundsBeGone.config.isAnalyticsEnabled()) {
            this.sendEvent("Started Minecraft", "");
        }
    }
    private void sendEvent(String event, String sound) {
        this.sendEvent(event, sound, Map.of());
    }

    private void sendEvent(String event, String sound, Map<String, Object> extraProps) {
        if (!SoundsBeGone.config.isAnalyticsEnabled()) {
            return;
        }

        Map<String, Object> baseProps = new HashMap<>(Map.of(
                "sound", sound,
                "Minecraft Version", MC_VERSION,
                "OS", OS_NAME,
                "Local Time", new java.util.Date().toString(),
                "ModVersion", SoundsBeGone.VERSION,
                "Java Version", JAVA_VERSION
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

    public void flush() {
        SoundsBeGone.LOGGER.debug("Flushing analytics");
        this.blockedSoundsCount.forEach((sound, count) -> {
            this.sendEvent("Blocked Sound", sound, Map.of(
                    "count", count
            ));
            this.blockedSoundsCount.remove(sound);
        });
    }
}
