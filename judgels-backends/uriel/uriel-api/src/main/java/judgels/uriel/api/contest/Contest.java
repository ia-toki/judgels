package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContest.class)
public interface Contest {
    long getId();
    String getJid();
    String getSlug();
    String getName();
    ContestStyle getStyle();
    Instant getBeginTime();
    Duration getDuration();

    default Instant getEndTime() {
        return getBeginTime().plus(getDuration());
    }

    class Builder extends ImmutableContest.Builder {}
}
