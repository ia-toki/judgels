package judgels.persistence;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

public class FixedClock extends Clock {
    private final Instant now;

    public FixedClock() {
        this(42);
    }

    public FixedClock(long now) {
        this.now = new Date(now).toInstant();
    }

    @Override
    public ZoneId getZone() {
        return ZoneId.systemDefault();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return now;
    }
}
