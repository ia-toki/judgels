package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemStatsEntry.class)
public interface ProblemStatsEntry {
    String getUserJid();
    int getStats();

    class Builder extends ImmutableProblemStatsEntry.Builder {}
}
