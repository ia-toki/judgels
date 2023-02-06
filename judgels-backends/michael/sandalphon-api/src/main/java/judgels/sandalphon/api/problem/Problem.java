package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblem.class)
public interface Problem {
    long getId();
    String getJid();
    String getSlug();
    String getAuthorJid();
    String getAdditionalNote();
    Instant getLastUpdateTime();
    ProblemType getType();

    class Builder extends ImmutableProblem.Builder {}
}
