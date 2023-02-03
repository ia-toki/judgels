package judgels.uriel.api.contest.announcement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestAnnouncementsResponse.class)
public interface ContestAnnouncementsResponse {
    Page<ContestAnnouncement> getData();
    Map<String, Profile> getProfilesMap();
    ContestAnnouncementConfig getConfig();

    class Builder extends ImmutableContestAnnouncementsResponse.Builder {}
}
