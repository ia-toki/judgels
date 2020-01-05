package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemTopStats.class)
public interface ProblemTopStats {
    List<ProblemTopStatsEntry> getTopUsersByScore();
    List<ProblemTopStatsEntry> getTopUsersByTime();
    List<ProblemTopStatsEntry> getTopUsersByMemory();

    class Builder extends ImmutableProblemTopStats.Builder {}
}
