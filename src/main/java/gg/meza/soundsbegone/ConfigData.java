package gg.meza.soundsbegone;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigData {
    public String lastVersionSeen = "";
    public boolean telemetry = true;
    public double frequencyPercentage = 10;
    public Map<String, Integer> soundVolumes = new HashMap<>();
    public Set<String> sounds = new HashSet<>(); // Legacy field, migrated to soundVolumes on load
    public Set<String> infrequent = new HashSet<>();
}
