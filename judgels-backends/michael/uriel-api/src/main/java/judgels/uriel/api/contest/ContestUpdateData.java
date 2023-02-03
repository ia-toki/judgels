package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestUpdateData.class)
public interface ContestUpdateData {
    Optional<String> getSlug();
    Optional<String> getName();
    Optional<ContestStyle> getStyle();
    Optional<Instant> getBeginTime();
    Optional<Duration> getDuration();

    class Builder extends ImmutableContestUpdateData.Builder {}
}
