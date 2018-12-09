package judgels.uriel.api.contest.supervisor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestSupervisorsResponse.class)
public interface ContestSupervisorsResponse {
    Page<ContestSupervisor> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableContestSupervisorsResponse.Builder {}
}
