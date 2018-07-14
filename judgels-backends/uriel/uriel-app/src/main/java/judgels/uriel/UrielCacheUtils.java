package judgels.uriel;

import java.time.Duration;

public class UrielCacheUtils {
    public static final String SEPARATOR = "#";

    private static Duration shortDuration = Duration.ofSeconds(5);
    private static Duration mediumDuration = Duration.ofSeconds(10);

    private UrielCacheUtils() {}

    public static Duration getShortDuration() {
        return shortDuration;
    }

    public static Duration getMediumDuration() {
        return mediumDuration;
    }

    // Visible for testing
    public static void removeDurations() {
        shortDuration = Duration.ZERO;
        mediumDuration = Duration.ZERO;
    }
}
