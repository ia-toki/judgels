package judgels.jerahmeel.api.problemset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSet.class)
public interface ProblemSet {
    long getId();
    String getJid();
    String getArchiveJid();
    String getSlug();
    String getName();
    String getDescription();
    Instant getContestTime();

    class Builder extends ImmutableProblemSet.Builder {}
}
