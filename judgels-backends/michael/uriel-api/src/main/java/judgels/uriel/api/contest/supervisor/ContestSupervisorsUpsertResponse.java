package judgels.uriel.api.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisorsUpsertResponse.class)
public interface ContestSupervisorsUpsertResponse {
    Map<String, Profile> getUpsertedSupervisorProfilesMap();

    class Builder extends ImmutableContestSupervisorsUpsertResponse.Builder {}
}
