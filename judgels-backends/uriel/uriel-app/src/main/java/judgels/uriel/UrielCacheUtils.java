package judgels.uriel;

import java.time.Duration;

public class UrielCacheUtils {
    public static final String SEPARATOR = "#";

    private static Duration shortDuration = Duration.ofSeconds(5);

    private UrielCacheUtils() {}

    public static Duration getShortDuration() {
        return shortDuration;
    }

    // Visible for testing
    public static void removeDurations() {
        shortDuration = Duration.ZERO;
    }
}
