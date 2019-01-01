package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProblem.class)
public interface ContestProblem {
    String getAlias();
    String getProblemJid();
    ContestProblemStatus getStatus();
    long getSubmissionsLimit();

    class Builder extends ImmutableContestProblem.Builder {}
}
