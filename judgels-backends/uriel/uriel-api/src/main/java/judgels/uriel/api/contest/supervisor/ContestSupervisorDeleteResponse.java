package judgels.uriel.api.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisorDeleteResponse.class)
public interface ContestSupervisorDeleteResponse {
    Map<String, Profile> getDeletedSupervisorProfilesMap();

    class Builder extends ImmutableContestSupervisorDeleteResponse.Builder {}
}
