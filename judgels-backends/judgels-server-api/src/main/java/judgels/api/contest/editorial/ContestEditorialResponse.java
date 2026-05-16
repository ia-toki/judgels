package judgels.api.contest.editorial;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.api.contest.problem.ContestProblem;
import judgels.api.problem.ProblemEditorialInfo;
import judgels.api.problem.ProblemInfo;
import judgels.api.problem.ProblemMetadata;
import judgels.api.profile.Profile;
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
