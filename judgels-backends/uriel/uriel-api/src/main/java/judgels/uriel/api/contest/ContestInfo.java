package judgels.uriel.api.contest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestInfo.class)
public interface ContestInfo {
    String getSlug();
    String getName();
    Instant getBeginTime();

    class Builder extends ImmutableContestInfo.Builder {}
}
