package judgels.jerahmeel.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.uriel.api.contest.ContestInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemMetadataResponse.class)
public interface ProblemMetadataResponse {
    ProblemInfo getProblem();
    List<ContestInfo> getContests();

    class Builder extends ImmutableProblemMetadataResponse.Builder {}
}
