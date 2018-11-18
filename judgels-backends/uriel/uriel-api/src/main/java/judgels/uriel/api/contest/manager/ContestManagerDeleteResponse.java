package judgels.uriel.api.contest.manager;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestManagerDeleteResponse.class)
public interface ContestManagerDeleteResponse {
    Map<String, Profile> getDeletedManagerProfilesMap();

    class Builder extends ImmutableContestManagerDeleteResponse.Builder {}
}
