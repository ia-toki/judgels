package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestData.class)
public interface ContestData {
    String getName();
    Optional<String> getSlug();
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
