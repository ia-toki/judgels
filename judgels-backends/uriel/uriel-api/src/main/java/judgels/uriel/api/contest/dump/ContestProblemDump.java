package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.persistence.api.dump.Dump;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProblemDump.class)
public interface ContestProblemDump extends Dump {
    String getAlias();
    String getProblemJid();
    ContestProblemStatus getStatus();
    long getSubmissionsLimit();
    Optional<Integer> getPoints();

    class Builder extends ImmutableContestProblemDump.Builder {}
}
