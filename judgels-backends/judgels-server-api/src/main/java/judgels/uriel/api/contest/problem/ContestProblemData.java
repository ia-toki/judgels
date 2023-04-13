package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProblemData.class)
public interface ContestProblemData {
    String getAlias();
    String getSlug();
    ContestProblemStatus getStatus();
    Optional<Long> getSubmissionsLimit();
    Optional<Integer> getPoints();

    class Builder extends ImmutableContestProblemData.Builder {}
}
