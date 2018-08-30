package judgels.uriel.api.contest.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestWebConfig.class)
public interface ContestWebConfig {
    Set<ContestTab> getVisibleTabs();
    ContestState getContestState();
    Optional<Duration> getRemainingContestStateDuration();
    long getAnnouncementCount();
    long getClarificationCount();
    ContestClarificationStatus getClarificationStatus();

    class Builder extends ImmutableContestWebConfig.Builder {}
}
