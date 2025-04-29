package judgels.persistence.h2;

import java.sql.Timestamp;
import java.time.Instant;

public class H2SqlFunctions {
    private H2SqlFunctions() {}

    public static long unixTimestamp(Timestamp ts) {
        if (ts == null) {
            return 0;
        }
        return ts.getTime() / 1000L;
    }

    public static Timestamp fromUnixTime(long epochSeconds) {
        return Timestamp.from(Instant.ofEpochSecond(epochSeconds));
    }
}
