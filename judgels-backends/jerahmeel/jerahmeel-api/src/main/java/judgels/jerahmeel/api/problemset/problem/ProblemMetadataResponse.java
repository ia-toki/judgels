package judgels.jerahmeel.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.ProblemMetadata;
import judgels.uriel.api.contest.ContestInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemMetadataResponse.class)
public interface ProblemMetadataResponse {
    ProblemMetadata getMetadata();
    List<ContestInfo> getContests();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableProblemMetadataResponse.Builder {}
}
