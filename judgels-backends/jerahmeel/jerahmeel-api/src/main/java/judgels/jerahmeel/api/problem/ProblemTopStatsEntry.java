package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemTopStatsEntry.class)
public interface ProblemTopStatsEntry {
    String getUserJid();
    int getStats();

    class Builder extends ImmutableProblemTopStatsEntry.Builder {}
}
