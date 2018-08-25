package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestData.class)
public interface ContestData {
    Optional<String> getSlug();
    String getName();
    String getDescription();
    ContestStyle getStyle();
    Instant getBeginTime();
    Duration getDuration();

    class Builder extends ImmutableContestData.Builder {
        public Builder() {
            description("");
            name("Unnamed Contest");
            style(ContestStyle.ICPC);
            beginTime(Instant.ofEpochSecond(4102444800L)); // 1 January 2100
            duration(Duration.ofHours(5));
        }
    }
}
