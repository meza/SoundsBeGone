package gg.meza.analytics;

import com.posthog.java.PostHog;
import gg.meza.SoundsBeGone;
import gg.meza.SoundsBeGoneClient;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;

public class Analytics {
    private static final String POSTHOG_API_KEY = "POSTHOG_API_KEY_REPL";
    private static final String POSTHOG_HOST = "https://eu.posthog.com";
    private PostHog posthog;

    // Not actually sending any user info, just using the uuid to create a new uuid that cannot be traced back to the user
    private final String uuid = DigestUtils.sha256Hex(MinecraftClient.getInstance().getSession().getUsername());
    private final String OS_NAME = System.getProperty("os.name");
    private final String MC_VERSION = MinecraftClient.getInstance().getGameVersion();
    private final String JAVA_VERSION = System.getProperty("java.version");
    public Analytics() {
        this.posthog = new PostHog.Builder(POSTHOG_API_KEY).host(POSTHOG_HOST).build();
        if (SoundsBeGoneClient.config.isAnalyticsEnabled()) {
            this.sendEvent("Started Minecraft", "");
        }
    }

    private void sendEvent(String event, String sound) {
        if (!SoundsBeGoneClient.config.isAnalyticsEnabled()) {
            return;
        }

        this.posthog.capture(this.uuid, event, new HashMap<String, Object>() {
            {
                put("sound", sound);
                put("Minecraft Version", MC_VERSION);
                put("OS", OS_NAME);
                put("Local Time", new java.util.Date().toString());
                put("ModVersion", SoundsBeGone.VERSION);
                put("Java Version", JAVA_VERSION);
            }
        });
    }

    public void mutedSound(String sound) {
        this.sendEvent("Muted Sound", sound);
    }

    public void blockedSound(String sound) {
        this.sendEvent("Blocked Sound", sound);
    }

    public void unmutedSound(String sound) {
        this.sendEvent("UnMuted Sound", sound);
    }
}
