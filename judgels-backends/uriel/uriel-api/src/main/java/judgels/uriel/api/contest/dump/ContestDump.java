package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import judgels.persistence.api.dump.JudgelsDump;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestDump.class)
public interface ContestDump extends JudgelsDump {
    String getSlug();
    String getName();
    Instant getBeginTime();
    Duration getDuration();
    String getDescription();

    ContestStyleDump getStyle();

    Set<ContestModuleDump> getModules();
    Set<ContestProblemDump> getProblems();
    Set<ContestContestantDump> getContestants();
    Set<ContestSupervisorDump> getSupervisors();
    Set<ContestManagerDump> getManagers();
    Set<ContestAnnouncementDump> getAnnouncements();
    Set<ContestClarificationDump> getClarifications();

    class Builder extends ImmutableContestDump.Builder {}
}
