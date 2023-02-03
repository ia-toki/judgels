package judgels.uriel.api.contest.announcement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestAnnouncement.class)
public interface ContestAnnouncement {
    long getId();
    String getJid();
    String getUserJid();
    String getTitle();
    String getContent();
    ContestAnnouncementStatus getStatus();
    Instant getUpdatedTime();

    class Builder extends ImmutableContestAnnouncement.Builder {}
}
