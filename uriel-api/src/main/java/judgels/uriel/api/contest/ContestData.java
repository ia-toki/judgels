package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestData.class)
public interface ContestData {
    String getName();
    String getDescription();
    ContestStyle getStyle();
    Instant getBeginTime();
    Duration getDuration();

    class Builder extends ImmutableContestData.Builder {
        public Builder() {
            description("");
            style(ContestStyle.IOI);
            beginTime(Instant.ofEpochSecond(1514764800));
            duration(Duration.ofHours(5));
        }
    }
}
