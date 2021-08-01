package judgels.uriel.api.contest.editorial;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.problem.ProblemEditorialInfo;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemMetadata;
import judgels.uriel.api.contest.problem.ContestProblem;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestEditorialResponse.class)
public interface ContestEditorialResponse {
    Optional<String> getPreface();
    List<ContestProblem> getProblems();
    Map<String, ProblemInfo> getProblemsMap();
    Map<String, ProblemEditorialInfo> getProblemEditorialsMap();
    Map<String, ProblemMetadata> getProblemMetadatasMap();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableContestEditorialResponse.Builder {}
}
