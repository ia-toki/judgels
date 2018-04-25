package judgels.uriel.api.contest.announcement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jophiel.api.user.UserInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestAnnouncementsResponse.class)
public interface ContestAnnouncementsResponse {
    List<ContestAnnouncement> getData();
    Map<String, UserInfo> getUsersMap();

    class Builder extends ImmutableContestAnnouncementsResponse.Builder {}
}
