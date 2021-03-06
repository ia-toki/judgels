package judgels.jerahmeel.api.problemset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetCreateData.class)
public interface ProblemSetCreateData {
    String getSlug();
    String getName();
    String getArchiveSlug();
    Optional<String> getDescription();
    Optional<Instant> getContestTime();

    class Builder extends ImmutableProblemSetCreateData.Builder {}
}
