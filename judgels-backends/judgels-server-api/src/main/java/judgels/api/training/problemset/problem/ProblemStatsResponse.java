package judgels.api.training.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import judgels.api.training.stats.ProblemProgress;
import judgels.api.training.stats.ProblemStats;
import judgels.api.training.stats.ProblemTopStats;
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
