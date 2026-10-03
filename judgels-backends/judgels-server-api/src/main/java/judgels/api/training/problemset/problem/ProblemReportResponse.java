package judgels.api.training.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.api.contest.ContestInfo;
import judgels.api.problem.ProblemMetadata;
import judgels.api.profile.Profile;
import judgels.api.training.stats.ProblemDifficulty;
import judgels.api.training.stats.ProblemProgress;
import judgels.api.training.stats.ProblemTopStats;
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
