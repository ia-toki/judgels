package judgels.jerahmeel;

import java.time.Duration;

public class JerahmeelCacheUtils {
    private static Duration shortDuration = Duration.ofSeconds(5);

    private JerahmeelCacheUtils() {}

    public static Duration getShortDuration() {
        return shortDuration;
    }
}
