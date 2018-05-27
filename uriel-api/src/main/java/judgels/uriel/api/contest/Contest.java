package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContest.class)
public interface Contest {
    long getId();
    String getJid();
    String getName();
    String getDescription();
    ContestStyle getStyle();
    Instant getBeginTime();
    Duration getDuration();

    default Instant getEndTime() {
        return getBeginTime().plus(getDuration());
    }

    default boolean hasStarted(Clock clock) {
        return !clock.instant().isBefore(getBeginTime());
    }

    default boolean hasFinished(Clock clock) {
        return !clock.instant().isBefore(getEndTime());
    }

    default boolean isRunning(Clock clock) {
        return hasStarted(clock) && !hasFinished(clock);
    }

    class Builder extends ImmutableContest.Builder {}
}
