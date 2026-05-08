package gg.meza.soundsbegone;

import java.util.HashSet;
import java.util.Set;

public class ConfigData {
    public String lastVersionSeen = "";
    public boolean telemetry = true;
    public double frequencyPercentage = 10;
    public Set<String> sounds = new HashSet<>();
    public Set<String> infrequent = new HashSet<>();
}
