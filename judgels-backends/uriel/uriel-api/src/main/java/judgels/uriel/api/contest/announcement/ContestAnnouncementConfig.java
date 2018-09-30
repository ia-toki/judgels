package judgels.uriel.api.contest.announcement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestAnnouncementConfig.class)
public interface ContestAnnouncementConfig {
    boolean getIsAllowedToCreateAnnouncement();
    boolean getIsAllowedToEditAnnouncement();

    class Builder extends ImmutableContestAnnouncementConfig.Builder {}
}
