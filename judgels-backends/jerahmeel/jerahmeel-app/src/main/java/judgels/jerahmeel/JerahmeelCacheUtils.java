package judgels.jerahmeel;

import java.time.Duration;

public class JerahmeelCacheUtils {
    public static final String SEPARATOR = "#";

    private static Duration shortDuration = Duration.ofSeconds(5);
    private static Duration longDuration = Duration.ofMinutes(10);

    private JerahmeelCacheUtils() {}

    public static Duration getShortDuration() {
        return shortDuration;
    }

    public static Duration getLongDuration() {
        return longDuration;
    }

    // Visible for testing
    public static void removeDurations() {
        shortDuration = Duration.ZERO;
        longDuration = Duration.ZERO;
    }
}
