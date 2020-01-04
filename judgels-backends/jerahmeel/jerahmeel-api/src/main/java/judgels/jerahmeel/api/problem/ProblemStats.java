package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemStats.class)
public interface ProblemStats {
    long getTotalUsersAccepted();
    long getTotalUsersTried();
    List<ProblemStatsEntry> getTopUsersByTime();
    List<ProblemStatsEntry> getTopUsersByMemory();

    class Builder extends ImmutableProblemStats.Builder {}
}
