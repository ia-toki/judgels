package judgels.jerahmeel.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jerahmeel.api.problem.ProblemDifficulty;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.api.problem.ProblemTopStats;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.problem.ProblemMetadata;
import judgels.uriel.api.contest.ContestInfo;
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
