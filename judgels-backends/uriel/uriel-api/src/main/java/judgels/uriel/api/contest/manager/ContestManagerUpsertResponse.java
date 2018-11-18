package judgels.uriel.api.contest.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestManagerUpsertResponse.class)
public interface ContestManagerUpsertResponse {
    Map<String, Profile> getInsertedManagerProfilesMap();
    Map<String, Profile> getAlreadyManagerProfilesMap();

    class Builder extends ImmutableContestManagerUpsertResponse.Builder {}
}
