package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProblem.class)
public interface ContestProblem {
    String getProblemJid();
    String getAlias();
    ContestProblemStatus getStatus();

    class Builder extends ImmutableContestProblem.Builder {}
}
