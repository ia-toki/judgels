package judgels.uriel.api.contest.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import judgels.uriel.api.contest.role.ContestRole;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestWebConfig.class)
public interface ContestWebConfig {
    ContestRole getRole();
    boolean canManage();
    Set<ContestTab> getVisibleTabs();
    ContestState getState();
    Optional<Duration> getRemainingStateDuration();
    long getAnnouncementCount();
    long getClarificationCount();
    ContestClarificationStatus getClarificationStatus();

    class Builder extends ImmutableContestWebConfig.Builder {}
}
