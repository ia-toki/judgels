package judgels.jerahmeel.api.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserTopStatsResponse.class)
public interface UserTopStatsResponse {
    UserTopStats getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableUserTopStatsResponse.Builder {}
}
