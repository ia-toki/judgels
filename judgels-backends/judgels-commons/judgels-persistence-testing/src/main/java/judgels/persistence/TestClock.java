package judgels.persistence;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class TestClock extends Clock {
    public static final Instant NOW = Instant.ofEpochMilli(42);

    private Instant now;

    public TestClock() {
        this(NOW);
    }

    public TestClock(Instant now) {
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

    public void tick(Duration duration) {
        now = now.plus(duration);
    }
}
