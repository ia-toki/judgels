package judgels.jerahmeel.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.uriel.api.contest.ContestInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemMetadataResponse.class)
public interface ProblemMetadataResponse {
    List<ContestInfo> getContests();

    class Builder extends ImmutableProblemMetadataResponse.Builder {}
}
