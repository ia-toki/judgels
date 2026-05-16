package judgels.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.api.contest.ContestInfo;
import judgels.api.problem.ProblemDifficulty;
import judgels.api.problem.ProblemMetadata;
import judgels.api.problem.ProblemProgress;
import judgels.api.problem.ProblemTopStats;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemReportResponse.class)
public interface ProblemReportResponse {
    ProblemMetadata getMetadata();
    ProblemDifficulty getDifficulty();
    ProblemTopStats getTopStats();
    ProblemProgress getProgress();
    List<ContestInfo> getContests();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemReportResponse.Builder {}
}
