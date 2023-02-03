package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.persistence.api.dump.JudgelsDump;
import judgels.uriel.api.contest.announcement.ContestAnnouncementStatus;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestAnnouncementDump.class)
public interface ContestAnnouncementDump extends JudgelsDump {
    String getTitle();
    String getContent();
    ContestAnnouncementStatus getStatus();

    class Builder extends ImmutableContestAnnouncementDump.Builder {}
}
