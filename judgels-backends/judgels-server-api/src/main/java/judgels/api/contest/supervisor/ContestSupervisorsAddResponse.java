package judgels.api.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisorsAddResponse.class)
public interface ContestSupervisorsAddResponse {
    Map<String, Profile> getAddedSupervisorProfilesMap();

    class Builder extends ImmutableContestSupervisorsAddResponse.Builder {}
}
