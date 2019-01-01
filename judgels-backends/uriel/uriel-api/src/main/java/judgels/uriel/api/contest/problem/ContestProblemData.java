package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProblemData.class)
public interface ContestProblemData {
    String getAlias();
    String getSlug();
    ContestProblemStatus getStatus();
    long getSubmissionsLimit();

    class Builder extends ImmutableContestProblemData.Builder {}
}
