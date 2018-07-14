package judgels.uriel.api.contest.announcement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestAnnouncementData.class)
public interface ContestAnnouncementData {
    String getTitle();
    String getContent();
    ContestAnnouncementStatus getStatus();

    class Builder extends ImmutableContestAnnouncementData.Builder {}
}
