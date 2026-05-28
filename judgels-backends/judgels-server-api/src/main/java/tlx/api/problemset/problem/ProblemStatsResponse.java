package tlx.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.problem.ProblemProgress;
import judgels.api.problem.ProblemStats;
import judgels.api.problem.ProblemTopStats;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemStatsResponse.class)
public interface ProblemStatsResponse {
    ProblemProgress getProgress();
    ProblemStats getStats();
    ProblemTopStats getTopStats();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemStatsResponse.Builder {}
}
