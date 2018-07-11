package judgels.persistence;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class FixedClock extends Clock {
    private final Instant now;

    public FixedClock() {
        this(Instant.ofEpochMilli(42));
    }

    public FixedClock(Instant now) {
        this.now = now;
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
