package gg.meza.soundsbegone.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SoundEmissionRegulator {
    private static final long WINDOW_MS = 5000;
    private static final long WINDOW_NS = TimeUnit.MILLISECONDS.toNanos(WINDOW_MS);

    private static final int MIN_ATTEMPTS = 1;
    private static final int MAX_ATTEMPTS = 100;

    private static class EmissionStats {
        // Ring buffer of attempt timestamps (monotonic nanoTime)
        final long[] times = new long[MAX_ATTEMPTS];
        int head = 0; // index of oldest
        int size = 0; // number of valid entries

        void add(long nowNs) {
            // Append at tail (or overwrite oldest when full)
            int tail = (head + size) % times.length;
            times[tail] = nowNs;

            if (size < times.length) {
                size++;
            } else {
                // full: overwrite oldest, advance head
                head = (head + 1) % times.length;
            }
        }

        void prune(long nowNs) {
            while (size > 0) {
                long oldest = times[head];
                if (nowNs - oldest <= WINDOW_NS) break;
                head = (head + 1) % times.length;
                size--;
            }
        }

        int countInWindow(long nowNs) {
            prune(nowNs);
            return size;
        }
    }

    private final Map<String, EmissionStats> stats = new ConcurrentHashMap<>();

    public double weightedChance(String sourceId, double baseFrequencyPercent) {
        final long now = System.nanoTime();

        final EmissionStats stat = stats.computeIfAbsent(sourceId, k -> new EmissionStats());

        final int attempts;
        synchronized (stat) {
            stat.add(now);
            attempts = stat.countInWindow(now);
        }

        int clamped = Math.max(MIN_ATTEMPTS, Math.min(attempts, MAX_ATTEMPTS));
        return baseFrequencyPercent / clamped;
    }
}
