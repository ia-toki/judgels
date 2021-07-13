package judgels.jerahmeel.api.stats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserTopStatsResponse.class)
public interface UserTopStatsResponse {
    Page<UserTopStatsEntry> getData();
    Map<String, Profile> getProfilesMap();

    class Builder extends ImmutableUserTopStatsResponse.Builder {}
}
