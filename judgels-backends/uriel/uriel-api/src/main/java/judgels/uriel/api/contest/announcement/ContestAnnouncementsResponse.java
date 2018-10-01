package judgels.uriel.api.contest.announcement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestAnnouncementsResponse.class)
public interface ContestAnnouncementsResponse {
    List<ContestAnnouncement> getData();
    ContestAnnouncementConfig getConfig();

    class Builder extends ImmutableContestAnnouncementsResponse.Builder {}
}
