package judgels.api.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisorsRemoveResponse.class)
public interface ContestSupervisorsRemoveResponse {
    Map<String, Profile> getRemovedSupervisorProfilesMap();

    class Builder extends ImmutableContestSupervisorsRemoveResponse.Builder {}
}
