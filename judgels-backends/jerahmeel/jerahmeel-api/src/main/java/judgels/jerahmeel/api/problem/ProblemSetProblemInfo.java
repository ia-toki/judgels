package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemInfo.class)
public interface ProblemSetProblemInfo {
    String getProblemSetSlug();
    String getProblemAlias();
    String getProblemJid();

    class Builder extends ImmutableProblemSetProblemInfo.Builder {}
}
