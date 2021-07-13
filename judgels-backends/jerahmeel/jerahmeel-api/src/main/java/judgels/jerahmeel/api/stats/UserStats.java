package judgels.jerahmeel.api.stats;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserStats.class)
public interface UserStats {
    int getTotalScores();
    int getTotalProblemsTried();
    Map<String, Long> getTotalProblemVerdictsMap();

    class Builder extends ImmutableUserStats.Builder {}
}
